package es.jmjg.experiments.infrastructure.config.jackson.tag;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.jmjg.experiments.domain.tag.entity.TagUuid;
import java.io.IOException;

public class TagUuidSerializer extends StdSerializer<TagUuid> {
  public TagUuidSerializer() {
    super(TagUuid.class);
  }

  @Override
  public void serialize(TagUuid value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeString(value.toString());
  }
}
