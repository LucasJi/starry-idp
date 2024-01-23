package cn.lucasji.starry.idp.core.modal.security;

import cn.lucasji.starry.idp.core.constant.SecurityConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.UrlUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author lucas
 * @date 2023/11/7 16:06
 */
@Slf4j
public class LoginTargetAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  public LoginTargetAuthenticationEntryPoint(String loginFormUrl) {
    super(loginFormUrl);
  }

  @Override
  public void commence(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException authException)
    throws IOException, ServletException {
    // 获取登录表单的地址
    String loginForm = determineUrlToUseForThisRequest(request, response, authException);
    log.info("登录表单地址:{}", loginForm);
    if (!UrlUtils.isAbsoluteUrl(loginForm)) {
      // 不是绝对路径调用父类方法处理
      super.commence(request, response, authException);
      return;
    }

    StringBuffer requestUrl = request.getRequestURL();
    if (!ObjectUtils.isEmpty(request.getQueryString())) {
      requestUrl.append("?").append(request.getQueryString());
    }

    // 重定向地址添加nonce参数，该参数的值为sessionId
    // 绝对路径在重定向前添加target参数
    String targetParameter = URLEncoder.encode(requestUrl.toString(), StandardCharsets.UTF_8);
    String targetUrl =
      loginForm
        + "?target="
        + targetParameter
        + "&"
        + SecurityConstants.NONCE_HEADER_NAME
        + "="
        + request.getSession(Boolean.FALSE).getId();
    log.info("重定向至前后端分离的登录页面, targetUrl:{}, targetParameter:{}", targetUrl,
      targetParameter);
    redirectStrategy.sendRedirect(request, response, targetUrl);
  }
}
