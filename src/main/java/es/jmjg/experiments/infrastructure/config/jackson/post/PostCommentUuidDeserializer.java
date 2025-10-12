package es.jmjg.experiments.infrastructure.config.jackson.post;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import java.io.IOException;

public class PostCommentUuidDeserializer extends StdDeserializer<PostCommentUuid> {
  public PostCommentUuidDeserializer() {
    super(PostCommentUuid.class);
  }

  @Override
  public PostCommentUuid deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return PostCommentUuid.fromString(p.getValueAsString());
  }
}
