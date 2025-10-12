package es.jmjg.experiments.infrastructure.config.jackson.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.io.IOException;

public class UserUuidSerializer extends StdSerializer<UserUuid> {
  public UserUuidSerializer() {
    super(UserUuid.class);
  }

  @Override
  public void serialize(UserUuid value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeString(value.toString());
  }
}
