package cn.lucasji.starry.idp.core.constant;

/**
 * @author lucas
 * @date 2023/11/7 21:22
 */
public class RedisConstants {

  /**
   * 认证信息存储前缀
   */
  public static final String SECURITY_CONTEXT_PREFIX_KEY = "security_context:";

  /**
   * 默认过期时间，默认五分钟
   */
  public static final long DEFAULT_TIMEOUT_SECONDS = 60L * 5;
}
