package cn.lucasji.starry.idp.core.entity;

import cn.lucas.starry.infrastructure.entity.BaseEntityAudit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author lucas
 * @date 2023/8/30 10:15
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "user", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseEntityAudit implements UserDetails, CredentialsContainer {

  @Serial
  private static final long serialVersionUID = -1239608698592368223L;

  @NotEmpty(message = "用户名不能为空")
  @Column(name = "username")
  private String username;

  @Email(message = "邮箱格式不正确")
  @Column(name = "email")
  private String email;

  /**
   * 密码字段不参与序列化（但参与反序列化）、不参与更新（但参与插入，因此更新密码需要额外接口去操作）
   *
   * <p>上述规则的建立是为了确保密码不会随便泄露出去
   */
  // TODO: feign调用user相关接口时, 该注解会影响序列化. 为了方便起见, 接口的入参回参直接接收实体类型, 后续如果改为DTO类型可以放开此注释
  // @JsonProperty(access = Access.WRITE_ONLY)
  @Column(updatable = false)
  private String password;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return new ArrayList<>();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void eraseCredentials() {
    password = null;
  }
}
