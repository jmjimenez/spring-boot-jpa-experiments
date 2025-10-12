package es.jmjg.experiments.infrastructure.config.jackson.post;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import java.io.IOException;

public class PostUuidDeserializer extends StdDeserializer<PostUuid> {
  public PostUuidDeserializer() {
    super(PostUuid.class);
  }

  @Override
  public PostUuid deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return PostUuid.fromString(p.getValueAsString());
  }
}
