package cn.lucasji.starry.idp.infrastructure.util;

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * @author lucas
 * @date 2023/10/22 21:43
 */
public abstract class AuthUtil {

  public static Long getUserIdFromJwt(Jwt jwt) {
    return Long.valueOf(jwt.getClaim("sub"));
  }
}
