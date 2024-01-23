package cn.lucasji.starry.idp.core.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lucas
 * @date 2023/8/25 13:46
 */
@ConfigurationProperties(prefix = "jwk")
@Configuration
@Getter
@Setter
public class JwkProperties {

  private String jksFilePath;

  private String alias;

  private String pass;
}
