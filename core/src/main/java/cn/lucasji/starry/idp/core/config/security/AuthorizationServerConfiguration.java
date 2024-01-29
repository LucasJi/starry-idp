package cn.lucasji.starry.idp.core.config.security;

import cn.lucasji.starry.idp.core.config.jackson2.mixin.RoleMixin;
import cn.lucasji.starry.idp.core.config.jackson2.mixin.TimestampMixin;
import cn.lucasji.starry.idp.core.config.jackson2.mixin.UserMixin;
import cn.lucasji.starry.idp.core.entity.Role;
import cn.lucasji.starry.idp.core.entity.User;
import cn.lucasji.starry.idp.core.modal.security.LoginFailureHandler;
import cn.lucasji.starry.idp.core.modal.security.LoginSuccessHandler;
import cn.lucasji.starry.idp.core.modal.security.LoginTargetAuthenticationEntryPoint;
import cn.lucasji.starry.idp.core.property.JwkProperties;
import cn.lucasji.starry.idp.core.support.RedisSecurityContextRepository;
import cn.lucasji.starry.idp.core.util.SecurityUtils;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.CorsFilter;

/**
 * @author lucas
 * @date 2023/8/23 20:03
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class AuthorizationServerConfiguration {

  @Value("${idp.login-url}")
  private String loginUrl;

  @Value("${idp.issuer}")
  private String idpIssuer;

  private final JwkProperties jwkProperties;

  private final RedisSecurityContextRepository redisSecurityContextRepository;

  private final BuildInRegisteredClients buildInRegisteredClients;

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        new OAuth2AuthorizationServerConfigurer();
    RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

    http.securityMatcher(endpointsMatcher)
        .securityContext(
            context -> context.securityContextRepository(redisSecurityContextRepository))
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        // 忽略认证端点的csrf校验
        .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
        // 未登录访问认证端点时重定向至login页面
        .exceptionHandling(
            exceptions ->
                exceptions.defaultAuthenticationEntryPointFor(
                    new LoginTargetAuthenticationEntryPoint(loginUrl),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
        // 处理使用access token访问用户信息端点和客户端注册端点
        .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
        // 开启OpenID Connect 1.0协议相关端点
        .with(
            authorizationServerConfigurer,
            configurer ->
                // 自定义oidc用户信息
                configurer.oidc(
                    oidc ->
                        oidc.userInfoEndpoint(
                            userInfo -> userInfo.userInfoMapper(getUserInfoMapper()))));
    return http.build();
  }

  /**
   * 将 jwt 中的用户信息映射为 oidc user 对象
   *
   * @return OidcUserInfoAuthenticationContext -> OidcUserInfo
   */
  Function<OidcUserInfoAuthenticationContext, OidcUserInfo> getUserInfoMapper() {
    return context -> {
      OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
      JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();

      log.info("===OidcUserInfo: {}===", authentication.getUserInfo());
      log.info("===JwtAuthenticationToken: {}===", principal);

      return new OidcUserInfo(principal.getToken().getClaims());
    };
  }

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, CorsFilter corsFilter)
      throws Exception {
    http.addFilter(corsFilter)
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/actuator/**", "/actuator")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        // 添加BearerTokenAuthenticationFilter,将认证服务当做一个资源服务,解析请求头中的token
        .oauth2ResourceServer(
            configurer ->
                configurer
                    .jwt(Customizer.withDefaults())
                    .accessDeniedHandler(SecurityUtils::exceptionHandler)
                    .authenticationEntryPoint(SecurityUtils::exceptionHandler))
        // 开启登录页面
        .formLogin(
            formLogin -> {
              formLogin.loginPage("/login");
              if (UrlUtils.isAbsoluteUrl(loginUrl)) {
                formLogin // 登录成功和失败改为写回json,不重定向了
                    .successHandler(new LoginSuccessHandler())
                    .failureHandler(new LoginFailureHandler());
              }
            })
        .securityContext(
            context -> context.securityContextRepository(redisSecurityContextRepository));
    return http.build();
  }

  /**
   * 配置密码解析器,使用BCrypt的方式对密码进行加密和验证
   *
   * @return BCryptPasswordEncoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
    JdbcRegisteredClientRepository registeredClientRepository =
        new JdbcRegisteredClientRepository(jdbcTemplate);

    // 初始化/更新clients
    for (RegisteredClient client : buildInRegisteredClients.getBuildInClients()) {
      registeredClientRepository.save(client);
    }

    return registeredClientRepository;
  }

  @Bean
  public OAuth2AuthorizationService oAuth2AuthorizationService(
      JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
    JdbcOAuth2AuthorizationService service =
        new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper =
        new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(registeredClientRepository);
    ObjectMapper objectMapper = new ObjectMapper();
    ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
    List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
    objectMapper.registerModules(securityModules);
    objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    // You will need to write the Mixin for your class so Jackson can marshall it.
    objectMapper.addMixIn(User.class, UserMixin.class);
    objectMapper.addMixIn(Role.class, RoleMixin.class);
    objectMapper.addMixIn(Timestamp.class, TimestampMixin.class);
    rowMapper.setObjectMapper(objectMapper);
    service.setAuthorizationRowMapper(rowMapper);

    return service;
  }

  @Bean
  public OAuth2AuthorizationConsentService authorizationConsentService(
      JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
    return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
  }

  @Bean
  @SneakyThrows
  public JWKSource<SecurityContext> jwkSource() {
    ClassPathResource resource = new ClassPathResource(jwkProperties.getJksFilePath());

    KeyStore jks = KeyStore.getInstance("jks");
    char[] pin = jwkProperties.getPass().toCharArray();
    jks.load(resource.getInputStream(), pin);
    RSAKey rsaKey = RSAKey.load(jks, jwkProperties.getAlias(), pin);

    return new ImmutableJWKSet<>(new JWKSet(rsaKey));
  }

  @SneakyThrows
  @Bean
  public JwtDecoder jwtDecoder() {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    // 读取cer公钥证书来配置解码器
    ClassPathResource resource = new ClassPathResource("pub.cer");
    Certificate certificate = certificateFactory.generateCertificate(resource.getInputStream());
    RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();
    return NimbusJwtDecoder.withPublicKey(publicKey).build();
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().issuer(idpIssuer).build();
  }
}
