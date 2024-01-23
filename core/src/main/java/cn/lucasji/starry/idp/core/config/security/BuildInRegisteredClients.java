package cn.lucasji.starry.idp.core.config.security;

import lombok.Getter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author lucas
 * @date 2023/8/29 22:57
 */
@Getter
public enum BuildInRegisteredClients {
  STARRY(
    RegisteredClient.withId("starry")
      .clientId("starry")
      .clientName("starry-client")
      // passwordEncoder.encode("starry")
      .clientSecret("$2a$10$.B57Cqa6gIszwQx.oRY5medUUOCJGNupi6z3DbFrfMYZ4bjhCKWcS")
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
      .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
      // debug oidc online
      .redirectUri("https://oidcdebugger.com/debug")
      // next-auth redirect uri format: [origin]/api/auth/callback/[provider]
      .redirectUri("http://localhost:3000/api/auth/callback/starry")
      .scopes(
        scopes ->
          scopes.addAll(
            Arrays.asList(
              OidcScopes.OPENID,
              OidcScopes.PROFILE,
              OidcScopes.EMAIL,
              "userinfo",
              "edu")))
      .tokenSettings(
        TokenSettings.builder()
          .accessTokenTimeToLive(Duration.ofMinutes(60))
          .refreshTokenTimeToLive(Duration.ofMinutes(120))
          .build())
      .clientSettings(
        ClientSettings.builder()
          // 客户端请求授权时是否添加“同意授权”选项
          .requireAuthorizationConsent(false)
          // 授权码授权流程中是否对密钥进行质询和验证
          .requireProofKey(false)
          .build())
      .build());

  BuildInRegisteredClients(RegisteredClient registeredClient) {
    this.registeredClient = registeredClient;
  }

  private final RegisteredClient registeredClient;
}
