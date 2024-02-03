package cn.lucasji.starry.idp.infrastructure.dto.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Lucas Ji
 * @date 2024/1/31 16:11
 */
@Data
public class FindUserPageReq implements Serializable {

  @Serial
  private static final long serialVersionUID = -199978386760644476L;

  List<Long> ids;

  private String username = "";

  private String email = "";
}
