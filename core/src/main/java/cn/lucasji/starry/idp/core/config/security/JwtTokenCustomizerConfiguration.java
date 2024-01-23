package cn.lucasji.starry.idp.core.config.security;

import cn.lucasji.starry.idp.core.service.OidcUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import static org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.ID_TOKEN;
import static org.springframework.security.oauth2.server.authorization.OAuth2TokenType.ACCESS_TOKEN;

/**
 * @author lucas
 * @date 2023/9/1 10:55
 */
@Configuration
@Slf4j
public class JwtTokenCustomizerConfiguration {

  /**
   * 自定义jwt内容
   *
   * @param userInfoService 获取当前用户信息
   * @return OAuth2TokenCustomizer
   */
  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(
    OidcUserInfoService userInfoService) {
    return context -> {
      log.info(
        "===authorization grant type: {}===", context.getAuthorizationGrantType().getValue());
      log.info("===token type: {}===", context.getTokenType().getValue());

      if (!AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())
        && (ID_TOKEN.equals(context.getTokenType().getValue())
        || ACCESS_TOKEN.equals(context.getTokenType()))) {
        OidcUserInfo userInfo = userInfoService.loadUser(context.getPrincipal().getName());
        context
          .getClaims()
          .claims(
            claims -> {
              log.info("===context claims: {}===", claims);
              log.info("===user info claims: {}===", userInfo.getClaims());

              claims.putAll(userInfo.getClaims());
            });
        context.getJwsHeader().type("jwt");
      }
    };
  }
}
