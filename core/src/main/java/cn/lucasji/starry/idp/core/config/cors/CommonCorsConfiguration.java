package cn.lucasji.starry.idp.core.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author lucas
 * @date 2023/12/5 10:38
 */
@Configuration
public class CommonCorsConfiguration {

  /**
   * 跨域过滤器配置
   *
   * @return CorsFilter
   */
  @Bean
  public CorsFilter corsFilter() {

    // 初始化cors配置对象
    CorsConfiguration configuration = new CorsConfiguration();

    // 设置允许跨域的域名,如果允许携带cookie的话,路径就不能写*号, *表示所有的域名都可以跨域访问
    // TODO 支持通过配置文件动态修改
    configuration.addAllowedOrigin("http://edu.lucasji.cn");
    // 设置跨域访问可以携带cookie，需要设置为 true 以满足 PKCE
    configuration.setAllowCredentials(true);
    // 允许所有的请求方法 ==> GET POST PUT Delete
    configuration.addAllowedMethod("*");
    // 允许携带任何头信息
    configuration.addAllowedHeader("*");

    // 初始化cors配置源对象
    UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();

    // 给配置源对象设置过滤的参数
    // 参数一: 过滤的路径 == > 所有的路径都要求校验是否跨域
    // 参数二: 配置类
    configurationSource.registerCorsConfiguration("/**", configuration);

    // 返回配置好的过滤器
    return new CorsFilter(configurationSource);
  }
}
