package es.jmjg.experiments.infrastructure.config.jackson.post;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import java.io.IOException;

public class PostCommentUuidSerializer extends StdSerializer<PostCommentUuid> {
  public PostCommentUuidSerializer() {
    super(PostCommentUuid.class);
  }

  @Override
  public void serialize(PostCommentUuid value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeString(value.toString());
  }
}
