package es.jmjg.experiments.infrastructure.config.jackson.post;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import java.io.IOException;

public class PostUuidSerializer extends StdSerializer<PostUuid> {
  public PostUuidSerializer() {
    super(PostUuid.class);
  }

  @Override
  public void serialize(PostUuid value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeString(value.toString());
  }
}
