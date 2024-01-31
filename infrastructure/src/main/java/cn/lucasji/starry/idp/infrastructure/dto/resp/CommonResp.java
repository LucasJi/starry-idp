package cn.lucasji.starry.idp.infrastructure.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author lucas
 * @date 2023/10/30 23:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResp implements Serializable {

  @Serial
  private static final long serialVersionUID = 4219102763662000571L;

  private boolean success;

  private String message;

  public static CommonResp success() {
    return new CommonResp(true, "");
  }

  public static CommonResp error(String message) {
    return new CommonResp(false, message);
  }
}
