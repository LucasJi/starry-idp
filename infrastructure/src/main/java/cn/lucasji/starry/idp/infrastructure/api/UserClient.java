package cn.lucasji.starry.idp.infrastructure.api;

import cn.lucasji.starry.idp.infrastructure.dto.AddUserDto;
import cn.lucasji.starry.idp.infrastructure.dto.EditUserDto;
import cn.lucasji.starry.idp.infrastructure.dto.UserDto;
import cn.lucasji.starry.idp.infrastructure.modal.Result;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author jiwh
 * @date 2024/1/30 13:27
 */
@FeignClient(name = "idp", url = "${idp.url}", path = "/user")
public interface UserClient {

  @GetMapping(path = "/{id}")
  UserDto findById(@PathVariable Long id);

  @PostMapping("/idUserMap")
  Map<Long, UserDto> getIdUserMapByUserIds(@RequestBody List<Long> ids);

  @PostMapping("/findPageByUserIdIn")
  Page<UserDto> findPageByUserIdIn(@RequestBody List<Long> userIds, Pageable pageable);

  @PostMapping
  Result<UserDto> addUser(@RequestBody AddUserDto body);

  @PatchMapping
  Result<String> updateUser(@RequestBody EditUserDto body);

  @DeleteMapping("/{id}")
  Result<String> deleteUser(@PathVariable Long id);
}
