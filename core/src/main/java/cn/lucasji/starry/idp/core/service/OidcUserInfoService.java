package cn.lucasji.starry.idp.core.service;

import cn.lucasji.starry.idp.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

/**
 * @author lucas
 * @date 2023/9/1 10:59
 */
@Service
@RequiredArgsConstructor
public class OidcUserInfoService {

  private final UserService userService;

  public OidcUserInfo loadUser(String username) {
    User user = userService.loadUserByUsername(username);
    return OidcUserInfo.builder()
      .subject(user.getId().toString())
      .name(user.getUsername())
      .email(user.getEmail())
      .emailVerified(true)
      .claims(claims -> claims.put("role", user.getRole().getName()))
      .build();
  }
}
