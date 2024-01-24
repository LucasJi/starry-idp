package cn.lucasji.starry.idp.core.support;

import static cn.lucasji.starry.idp.core.constant.RedisConstants.DEFAULT_TIMEOUT_SECONDS;
import static cn.lucasji.starry.idp.core.constant.RedisConstants.SECURITY_CONTEXT_PREFIX_KEY;
import static cn.lucasji.starry.idp.core.constant.SecurityConstants.NONCE_HEADER_NAME;

import cn.lucasji.starry.idp.core.modal.security.SupplierDeferredSecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

/**
 * @author lucas
 * @date 2023/11/7 21:20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSecurityContextRepository implements SecurityContextRepository {

  private final RedisOperator<SecurityContext> redisOperator;

  private final SecurityContextHolderStrategy securityContextHolderStrategy =
      SecurityContextHolder.getContextHolderStrategy();

  @Override
  @Deprecated
  public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
    // 方法已过时，使用 loadDeferredContext 方法
    throw new UnsupportedOperationException("Method deprecated.");
  }

  @Override
  public void saveContext(
      SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
    String nonce = getNonce(request);
    if (ObjectUtils.isEmpty(nonce)) {
      log.debug("the nonce from request is empty");
      return;
    }

    // 如果当前的context是空的，则移除
    SecurityContext emptyContext = securityContextHolderStrategy.createEmptyContext();
    if (emptyContext.equals(context)) {
      log.debug("delete empty context from redis");
      redisOperator.delete((SECURITY_CONTEXT_PREFIX_KEY + nonce));
    } else {
      // 保存认证信息
      log.debug("save security context in redis with nonce id {}", nonce);
      redisOperator.set((SECURITY_CONTEXT_PREFIX_KEY + nonce), context, DEFAULT_TIMEOUT_SECONDS);
    }
  }

  @Override
  public boolean containsContext(HttpServletRequest request) {
    String nonce = getNonce(request);
    if (ObjectUtils.isEmpty(nonce)) {
      log.debug("the nonce from request is empty");
      return false;
    }
    // 检验当前请求是否有认证信息
    log.debug("the nonce from request is {}", nonce);
    return redisOperator.get((SECURITY_CONTEXT_PREFIX_KEY + nonce)) != null;
  }

  @Override
  public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
    Supplier<SecurityContext> supplier = () -> readSecurityContextFromRedis(request);
    return new SupplierDeferredSecurityContext(supplier, securityContextHolderStrategy);
  }

  /**
   * 从redis中获取认证信息
   *
   * @param request 当前请求
   * @return 认证信息
   */
  private SecurityContext readSecurityContextFromRedis(HttpServletRequest request) {
    if (request == null) {
      return null;
    }

    String nonce = getNonce(request);
    if (ObjectUtils.isEmpty(nonce)) {
      return null;
    }

    // 根据缓存id获取认证信息
    return redisOperator.get((SECURITY_CONTEXT_PREFIX_KEY + nonce));
  }

  /**
   * 先从请求头中找，找不到去请求参数中找，找不到获取当前session的id
   *
   * @param request 当前请求
   * @return 随机字符串(sessionId)，这个字符串本来是前端生成，现在改为后端获取的sessionId
   */
  private String getNonce(HttpServletRequest request) {
    String nonce = request.getHeader(NONCE_HEADER_NAME);
    if (ObjectUtils.isEmpty(nonce)) {
      nonce = request.getParameter(NONCE_HEADER_NAME);
      HttpSession session = request.getSession(Boolean.FALSE);
      if (ObjectUtils.isEmpty(nonce) && session != null) {
        nonce = session.getId();
      }
    }
    return nonce;
  }
}
