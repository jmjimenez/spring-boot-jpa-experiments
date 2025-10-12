package es.jmjg.experiments.infrastructure.config.jackson.user;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.io.IOException;

public class UserUuidDeserializer extends StdDeserializer<UserUuid> {
  public UserUuidDeserializer() {
    super(UserUuid.class);
  }

  @Override
  public UserUuid deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return UserUuid.fromString(p.getValueAsString());
  }
}
