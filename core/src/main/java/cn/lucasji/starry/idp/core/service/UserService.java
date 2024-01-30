package cn.lucasji.starry.idp.core.service;

import cn.lucasji.starry.idp.core.entity.User;
import cn.lucasji.starry.idp.core.repository.UserRepository;
import cn.lucasji.starry.idp.infrastructure.dto.UserDto;
import cn.lucasji.starry.idp.infrastructure.modal.Result;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NoSuchElementException("Could not find user with id " + id));
    return UserDto.builder()
        .creationTimestamp(user.getCreationTimestamp())
        .id(user.getId())
        .email(user.getEmail())
        .username(user.getUsername())
        .build();
  }

  public Map<Long, UserDto> findIdUserMapByIds(List<Long> ids) {
    List<User> users = userRepository.findAllByIdIn(ids);
    return users.stream()
        .map(
            user ->
                UserDto.builder()
                    .creationTimestamp(user.getCreationTimestamp())
                    .id(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build())
        .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
  }

  public Page<UserDto> findPageByUserIdIn(List<Long> userIds, Pageable pageable) {
    Page<User> userPage = userRepository.findAllByIdIn(userIds, pageable);
    log.info("users: {}", userPage.getContent());
    return userPage.map(
        user ->
            UserDto.builder()
                .creationTimestamp(user.getCreationTimestamp())
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build());
  }

  public Result<UserDto> add(UserDto userDto) {
    log.info("新增用户:{}", userDto);

    if (userRepository.existsByEmail(userDto.getEmail())) {
      return Result.error("用户邮箱已存在");
    }

    userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    UserDto added = userRepository.save(userDto);
    return Result.success(added);
  }

  public Result<String> edit(UserDto userDto) {
    log.info("更新用户:{}", userDto);
    UserDto originalUserDto = findById(userDto.getId());

    if (!Objects.equals(userDto.getEmail(), originalUserDto.getEmail())) {
      log.info("用户邮箱更改:{}->{}", originalUserDto.getEmail(), userDto.getEmail());

      if (userRepository.existsByEmail(userDto.getEmail())) {
        return Result.error("用户邮箱已存在");
      }

      originalUserDto.setEmail(userDto.getEmail());
    }

    if (!Objects.equals(originalUserDto.getUsername(), userDto.getUsername())) {
      log.info("用户姓名更改:{}->{}", originalUserDto.getUsername(), userDto.getUsername());
      originalUserDto.setUsername(userDto.getUsername());
    }

    if (StringUtils.isNotBlank(userDto.getPassword())) {
      log.info("用户密码更新");
      originalUserDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    }

    userRepository.save(originalUserDto);

    return Result.success();
  }

  public Result<String> delete(Long userId) {
    userRepository.deleteById(userId);
    return Result.success();
  }
}
