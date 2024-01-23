package cn.lucasji.starry.idp.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Identity Provider Application
 *
 * @author lucas
 * @date 2023/8/23 17:24
 */
@SpringBootApplication
@Slf4j
public class IdpCoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(IdpCoreApplication.class, args);
  }
}
