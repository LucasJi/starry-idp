package cn.lucasji.starry.idp.core.controller;

import cn.lucasji.starry.idp.core.service.UserService;
import cn.lucasji.starry.idp.infrastructure.dto.UserDto;
import cn.lucasji.starry.idp.infrastructure.modal.Result;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public UserDto findById(@PathVariable Long id) {
    return userService.findById(id);
  }

  @PostMapping("/idUserMap")
  public Map<Long, UserDto> getIdUserMapByUserIds(@RequestBody List<Long> ids) {
    return userService.findIdUserMapByIds(ids);
  }

  @PostMapping("/findPageByUserIdIn")
  public Page<UserDto> findPageByUserIdIn(@RequestBody List<Long> userIds, Pageable pageable) {
    return userService.findPageByUserIdIn(userIds, pageable);
  }

  @PostMapping
  public Result<UserDto> add(@RequestBody UserDto userDto) {
    return userService.add(userDto);
  }

  @PatchMapping
  public Result<String> edit(@RequestBody UserDto userDto) {
    return userService.edit(userDto);
  }

  @DeleteMapping("/{id}")
  public Result<String> delete(@PathVariable Long id) {
    return userService.delete(id);
  }
}
