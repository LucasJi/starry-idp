package cn.lucasji.starry.idp.core.repository;

import cn.lucasji.starry.idp.core.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author lucas
 * @date 2023/9/1 13:39
 */
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  @Query(
    value = "UPDATE \"user\" SET password=:newPassword WHERE username=:username",
    nativeQuery = true)
  void updatePassword(String username, String newPassword);

  List<User> findAllByIdIn(List<Long> ids);

  Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

  boolean existsByEmail(String email);
}
