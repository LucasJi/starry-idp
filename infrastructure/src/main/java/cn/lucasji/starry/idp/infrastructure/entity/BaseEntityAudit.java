package cn.lucasji.starry.idp.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.util.Date;

/**
 * @author lucas
 * @date 2023/8/13 14:49
 */
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntityAudit extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1593446451587196116L;

  @CreationTimestamp
  @Column(name = "creation_timestamp", updatable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
  protected Date creationTimestamp;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
  @UpdateTimestamp
  protected Date updateTimestamp;
}
