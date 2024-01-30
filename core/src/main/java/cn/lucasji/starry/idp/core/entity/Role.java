package cn.lucasji.starry.idp.core.entity;

import cn.lucasji.starry.idp.infrastructure.entity.BaseEntityAudit;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author lucas
 * @date 2023/8/30 10:18
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "role", schema = "public")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntityAudit {

  @Serial private static final long serialVersionUID = 5788075670411718009L;

  @NotEmpty(message = "角色名称不能为空")
  private String name;
}
