package cn.lucasji.starry.idp.core.modal.security;

import cn.lucas.starry.infrastructure.modal.Result;
import cn.lucas.starry.infrastructure.util.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * @author lucas
 * @date 2023/11/7 16:03
 */
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    log.info("登入失败,原因:{}", exception.getMessage());
    // 登录失败,写回401与具体的异常
    Result<String> success = Result.error(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(JsonUtils.objectCovertToJson(success));
    response.getWriter().flush();
  }
}
