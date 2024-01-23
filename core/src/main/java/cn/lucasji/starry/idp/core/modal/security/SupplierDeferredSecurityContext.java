package cn.lucasji.starry.idp.core.modal.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import java.util.function.Supplier;

/**
 * @author Steve Riesenberg
 * @since 5.8
 */
public final class SupplierDeferredSecurityContext implements DeferredSecurityContext {

  private static final Log logger = LogFactory.getLog(SupplierDeferredSecurityContext.class);

  private final Supplier<SecurityContext> supplier;

  private final SecurityContextHolderStrategy strategy;

  private SecurityContext securityContext;

  private boolean missingContext;

  public SupplierDeferredSecurityContext(
    Supplier<SecurityContext> supplier, SecurityContextHolderStrategy strategy) {
    this.supplier = supplier;
    this.strategy = strategy;
  }

  @Override
  public SecurityContext get() {
    init();
    return securityContext;
  }

  @Override
  public boolean isGenerated() {
    init();
    return missingContext;
  }

  private void init() {
    if (securityContext != null) {
      return;
    }

    securityContext = supplier.get();
    missingContext = (securityContext == null);
    if (missingContext) {
      securityContext = strategy.createEmptyContext();
      if (logger.isTraceEnabled()) {
        logger.trace(LogMessage.format("Created %s", securityContext));
      }
    }
  }
}
