package cn.lucasji.starry.idp.core.service;

import cn.lucasji.starry.idp.core.entity.Role;
import cn.lucasji.starry.idp.core.entity.User;
import cn.lucasji.starry.idp.core.mapper.UserMapper;
import cn.lucasji.starry.idp.core.repository.UserRepository;
import cn.lucasji.starry.idp.infrastructure.dto.UserDto;
import cn.lucasji.starry.idp.infrastructure.dto.req.AddUserReq;
import cn.lucasji.starry.idp.infrastructure.dto.req.EditUserReq;
import cn.lucasji.starry.idp.infrastructure.dto.req.FindUserPageReq;
import cn.lucasji.starry.idp.infrastructure.modal.Result;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lucas
 * @date 2023/9/1 13:38
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService, UserDetailsPasswordService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final EntityManager entityManager;

  private final UserMapper userMapper;

  @Override
  public User loadUserByUsername(String username) {
    return userRepository
      .findByUsername(username)
      .orElseThrow(
        () -> new UsernameNotFoundException("user with name '" + username + "' not found"));
  }

  @Override
  public User updatePassword(UserDetails user, String newPassword) {
    userRepository.updatePassword(user.getUsername(), passwordEncoder.encode(newPassword));
    entityManager.flush();
    return loadUserByUsername(user.getUsername());
  }

  public UserDto findById(Long id) {
    User user = findByIdOrElseThrow(id);
    return userMapper.convertToUserDto(user);
  }

  private User findByIdOrElseThrow(Long id) {
    return userRepository
      .findById(id)
      .orElseThrow(() -> new NoSuchElementException("Could not find user with id " + id));
  }

  public Map<Long, UserDto> findIdUserMapByIds(List<Long> ids) {
    List<User> users = userRepository.findAllByIdIn(ids);
    return users.stream()
      .map(userMapper::convertToUserDto)
      .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
  }

  public Page<UserDto> findPage(FindUserPageReq body, Pageable pageable) {
    Page<User> userPage = userRepository.findAllByIdInAndUsernameLikeIgnoreCaseAndEmailLikeIgnoreCase(
      body.getIds(), STR."%\{body.getUsername()}%", STR."%\{body.getEmail()}%", pageable);
    log.info("users: {}", userPage.getContent());
    return userPage.map(userMapper::convertToUserDto);
  }

  @Transactional(rollbackFor = Exception.class)
  public Result<Long> add(AddUserReq addUserReq) {
    log.info("新增用户:{}", addUserReq);

    if (userRepository.existsByEmail(addUserReq.getEmail())) {
      return Result.error("用户邮箱已存在");
    }

    User user = userMapper.convertedFrom(addUserReq);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    // Role: member
    user.setRole(Role.builder().id(2L).build());
    User save = userRepository.save(user);

    return Result.success(save.getId());
  }

  public Result<String> edit(EditUserReq editUserReq) {
    log.info("更新用户:{}", editUserReq);
    User userFromDatasource = findByIdOrElseThrow(editUserReq.getId());

    if (!Objects.equals(editUserReq.getEmail(), userFromDatasource.getEmail())) {
      log.info("用户邮箱更改:{}->{}", userFromDatasource.getEmail(), editUserReq.getEmail());

      if (userRepository.existsByEmail(editUserReq.getEmail())) {
        return Result.error("用户邮箱已存在");
      }

      userFromDatasource.setEmail(editUserReq.getEmail());
    }

    if (!Objects.equals(editUserReq.getUsername(), userFromDatasource.getUsername())) {
      log.info("用户姓名更改:{}->{}", userFromDatasource.getUsername(), editUserReq.getUsername());
      userFromDatasource.setUsername(editUserReq.getUsername());
    }

    userRepository.save(userFromDatasource);

    return Result.success();
  }

  public Result<String> delete(Long userId) {
    userRepository.deleteById(userId);
    return Result.success();
  }
}
