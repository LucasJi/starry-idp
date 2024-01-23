package cn.lucasji.starry.idp.core.config.jackson2.deserializer;

import cn.lucasji.starry.idp.core.entity.Role;
import cn.lucasji.starry.idp.core.entity.User;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lucas
 * @date 2023/9/4 10:18
 */
@Slf4j
public class UserDeserializer extends JsonDeserializer<User> {

  @Override
  public User deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
    ObjectMapper mapper = (ObjectMapper) jp.getCodec();
    JsonNode jsonNode = mapper.readTree(jp);
    JsonNode passwordNode = readJsonNode(jsonNode, "password");
    String username = readJsonNode(jsonNode, "username").asText();
    String password = passwordNode.asText("");
    String email = readJsonNode(jsonNode, "email").asText();
    Long id = readJsonNode(jsonNode, "id").asLong();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Date creationTimestamp = null;
    Date updateTimestamp = null;
    try {
      creationTimestamp = sdf.parse(readJsonNode(jsonNode, "creationTimestamp").get(1).asText());
      updateTimestamp = sdf.parse(readJsonNode(jsonNode, "updateTimestamp").get(1).asText());
    } catch (ParseException e) {
      log.error("反序列化User creationTimestamp/updateTimestamp失败", e);
    }
    Role role = mapper.convertValue(readJsonNode(jsonNode, "role"), Role.class);

    User result =
      User.builder()
        .id(id)
        .username(username)
        .email(email)
        .password(password)
        .creationTimestamp(creationTimestamp)
        .updateTimestamp(updateTimestamp)
        .role(role)
        .build();

    log.info("反序列化User成功: {}", result);

    if (passwordNode.asText(null) == null) {
      result.eraseCredentials();
    }

    return result;
  }

  private JsonNode readJsonNode(JsonNode jsonNode, String field) {
    return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
  }
}
