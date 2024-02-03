package cn.lucasji.starry.idp.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lucas Ji
 * @date 2024/1/30 13:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
  private Long id;

  private String username;

  private String email;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
  private Date creationTimestamp;
}
