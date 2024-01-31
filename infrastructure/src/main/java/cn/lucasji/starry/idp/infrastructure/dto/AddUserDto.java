package cn.lucasji.starry.idp.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * @author jiwh
 * @date 2024/1/31 9:35
 */
@Data
public class AddUserDto implements Serializable {

  @Serial private static final long serialVersionUID = 759718627835795470L;

  @NotEmpty(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;

  @NotEmpty(message = "密码不能为空")
  private String password;

  @NotEmpty(message = "用户名不能为空")
  private String username;
}
