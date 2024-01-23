package cn.lucasji.starry.idp.infrastructure.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author lucas
 * @date 2023/8/13 10:51
 */
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 3815999466245327659L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;
}
