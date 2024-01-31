package cn.lucasji.starry.idp.infrastructure.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * @author jiwh
 * @date 2024/1/31 9:48
 */
@Data
public class EditUserDto implements Serializable {

  @Serial private static final long serialVersionUID = -691382313286887000L;

  private Long id;

  private String email;

  private String username;
}
