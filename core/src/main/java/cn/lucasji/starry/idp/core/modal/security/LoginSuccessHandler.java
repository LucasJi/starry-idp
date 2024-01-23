package cn.lucasji.starry.idp.core.modal.security;

import cn.lucas.starry.infrastructure.modal.Result;
import cn.lucas.starry.infrastructure.util.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author lucas
 * @date 2023/11/7 15:43
 */
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(
    HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    throws IOException {
    log.info("登录成功.");
    Result<String> success = Result.success();
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(JsonUtils.objectCovertToJson(success));
    response.getWriter().flush();
  }
}
