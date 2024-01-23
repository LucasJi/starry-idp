package cn.lucasji.starry.idp.core.config.jackson2.mixin;

import cn.lucasji.starry.idp.core.config.jackson2.deserializer.RoleDeserializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * For {@code OAuth2AuthorizationService}, see details from the <a
 * href="https://github.com/spring-projects/spring-authorization-server/issues/397">issue</a>.
 *
 * @author lucas
 * @date 2023/9/4 10:05
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = RoleDeserializer.class)
@JsonAutoDetect(
  fieldVisibility = JsonAutoDetect.Visibility.ANY,
  getterVisibility = JsonAutoDetect.Visibility.NONE,
  isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RoleMixin {

}
