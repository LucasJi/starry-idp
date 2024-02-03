package cn.lucasji.starry.idp.infrastructure.dto.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Lucas Ji
 * @date 2024/1/31 9:48
 */
@Data
public class EditUserReq implements Serializable {

  @Serial
  private static final long serialVersionUID = -691382313286887000L;

  private Long id;

  private String email;

  private String username;
}
