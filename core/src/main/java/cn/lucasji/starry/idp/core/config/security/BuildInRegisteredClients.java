package cn.lucasji.starry.idp.core.config.security;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

/**
 * @author lucas
 * @date 2023/8/29 22:57
 */
@Component
public class BuildInRegisteredClients {

  @Value("${idp.registered-client.starry-edu.redirect-url}")
  private String starryEduClientRedirectUrl;

  @Value("${idp.registered-client.starry-edu.client-secret}")
  private String starryEduClientSecret;

  public List<RegisteredClient> getBuildInClients() {
    List<RegisteredClient> buildInClients = new ArrayList<>();

    // starry edu client
    RegisteredClient eduClient =
        RegisteredClient.withId("starry-edu")
            .clientId("starry-edu")
            .clientName("starry-edu")
            // passwordEncoder.encode("starry")
            .clientSecret(starryEduClientSecret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            // next-auth redirect uri format: [origin]/api/auth/callback/[provider]
            .redirectUri(starryEduClientRedirectUrl)
            .scopes(
                scopes ->
                    scopes.addAll(
                        Arrays.asList(
                            OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL, "userinfo")))
            .tokenSettings(
                TokenSettings.builder().accessTokenTimeToLive(Duration.ofDays(1)).build())
            .clientSettings(
                ClientSettings.builder()
                    // 客户端请求授权时是否添加“同意授权”选项
                    .requireAuthorizationConsent(false)
                    // 授权码授权流程中是否对密钥进行质询和验证
                    .requireProofKey(false)
                    .build())
            .build();

    buildInClients.add(eduClient);

    return buildInClients;
  }
}
