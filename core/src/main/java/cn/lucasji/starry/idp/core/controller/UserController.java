package cn.lucasji.starry.idp.core.controller;

import cn.lucas.starry.infrastructure.modal.Result;
import cn.lucasji.starry.idp.core.entity.User;
import cn.lucasji.starry.idp.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author lucas
 * @date 2023/8/25 14:45
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{id}")
  public User findById(@PathVariable Long id) {
    return userService.findById(id);
  }

  @PostMapping("/idUserMap")
  public Map<Long, User> getIdUserMapByUserIds(@RequestBody List<Long> ids) {
    return userService.getIdUserMapByIds(ids);
  }

  @PostMapping("/findPageByUserIdIn")
  public Page<User> findPageByUserIdIn(@RequestBody List<Long> userIds, Pageable pageable) {
    return userService.findPageByUserIdIn(userIds, pageable);
  }

  @PostMapping("/addUser")
  public Result<User> addUser(@RequestBody User user) {
    return userService.addUser(user);
  }

  @PostMapping("/updateUser")
  public Result<String> updateUser(@RequestBody User user) {
    return userService.updateUser(user);
  }

  @DeleteMapping("/deleteUser/{id}")
  public Result<String> deleteUser(@PathVariable Long id) {
    return userService.deleteUser(id);
  }
}
