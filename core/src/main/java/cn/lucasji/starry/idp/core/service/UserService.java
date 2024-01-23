package cn.lucasji.starry.idp.core.service;

import cn.lucas.starry.infrastructure.entity.BaseEntity;
import cn.lucas.starry.infrastructure.modal.Result;
import cn.lucasji.starry.idp.core.entity.User;
import cn.lucasji.starry.idp.core.repository.UserRepository;
import jakarta.persistence.EntityManager;
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

  public User findById(Long id) {
    return userRepository
      .findById(id)
      .orElseThrow(() -> new NoSuchElementException("Could not find user with id " + id));
  }

  public Map<Long, User> getIdUserMapByIds(List<Long> ids) {
    List<User> users = userRepository.findAllByIdIn(ids);
    return users.stream().collect(Collectors.toMap(BaseEntity::getId, user -> user));
  }

  public Page<User> findPageByUserIdIn(List<Long> userIds, Pageable pageable) {
    Page<User> userPage = userRepository.findAllByIdIn(userIds, pageable);
    log.info("users: {}", userPage.getContent());
    return userPage;
  }

  public Result<User> addUser(User user) {
    log.info("新增用户:{}", user);

    if (userRepository.existsByEmail(user.getEmail())) {
      return Result.error("用户邮箱已存在");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    User added = userRepository.save(user);
    return Result.success(added);
  }

  public Result<String> updateUser(User user) {
    log.info("更新用户:{}", user);
    User originalUser = findById(user.getId());

    if (!Objects.equals(user.getEmail(), originalUser.getEmail())) {
      log.info("用户邮箱更改:{}->{}", originalUser.getEmail(), user.getEmail());

      if (userRepository.existsByEmail(user.getEmail())) {
        return Result.error("用户邮箱已存在");
      }

      originalUser.setEmail(user.getEmail());
    }

    if (!Objects.equals(originalUser.getUsername(), user.getUsername())) {
      log.info("用户姓名更改:{}->{}", originalUser.getUsername(), user.getUsername());
      originalUser.setUsername(user.getUsername());
    }

    if (StringUtils.isNotBlank(user.getPassword())) {
      log.info("用户密码更新");
      originalUser.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    userRepository.save(originalUser);

    return Result.success();
  }

  public Result<String> deleteUser(Long userId) {
    userRepository.deleteById(userId);
    return Result.success();
  }
}
