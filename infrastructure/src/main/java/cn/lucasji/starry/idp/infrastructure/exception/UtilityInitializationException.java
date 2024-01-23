package cn.lucasji.starry.idp.infrastructure.exception;

import java.io.Serial;

/**
 * @author lucas
 * @date 2023/11/7 15:49
 */
public class UtilityInitializationException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -7987942406992865255L;

  public UtilityInitializationException() {
    super("Utility classes cannot be instantiated.");
  }
}
