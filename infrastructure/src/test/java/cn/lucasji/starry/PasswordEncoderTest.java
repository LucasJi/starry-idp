package cn.lucasji.starry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author lucas
 * @date 2023/8/31 11:30
 */
class PasswordEncoderTest {

  @Test
  void bcrypt() {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String password = "starry";
    String encode = passwordEncoder.encode(password);
    System.out.println(encode);
    Assertions.assertTrue(passwordEncoder.matches(password, encode));
  }

  @Test
  void matches() {
    String encoded = "$2a$10$.B57Cqa6gIszwQx.oRY5medUUOCJGNupi6z3DbFrfMYZ4bjhCKWcS";
    String original = "starry";
    Assertions.assertTrue(new BCryptPasswordEncoder().matches(original, encoded));
  }
}
