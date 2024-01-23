package cn.lucasji.starry.idp.core.config.jackson2.deserializer;

import cn.lucasji.starry.idp.core.entity.Role;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.io.IOException;

/**
 * @author lucas
 * @date 2023/9/4 10:18
 */
public class RoleDeserializer extends JsonDeserializer<Role> {

  @Override
  public Role deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
    ObjectMapper mapper = (ObjectMapper) jp.getCodec();
    JsonNode jsonNode = mapper.readTree(jp);
    String name = readJsonNode(jsonNode, "name").asText();
    Long id = readJsonNode(jsonNode, "id").asLong();
    return Role.builder().id(id).name(name).build();
  }

  private JsonNode readJsonNode(JsonNode jsonNode, String field) {
    return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
  }
}
