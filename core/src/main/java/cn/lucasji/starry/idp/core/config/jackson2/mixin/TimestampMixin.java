package cn.lucasji.starry.idp.core.config.jackson2.mixin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.TimestampDeserializer;

/**
 * For {@code OAuth2AuthorizationService}, see details from the <a
 * href="https://github.com/spring-projects/spring-authorization-server/issues/397">issue</a>.
 *
 * @author lucas
 * @date 2023/9/4 10:05
 */
@JsonTypeInfo(use = Id.CLASS)
@JsonDeserialize(using = TimestampDeserializer.class)
@JsonAutoDetect(
  fieldVisibility = JsonAutoDetect.Visibility.ANY,
  getterVisibility = JsonAutoDetect.Visibility.NONE,
  isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class TimestampMixin {

}
