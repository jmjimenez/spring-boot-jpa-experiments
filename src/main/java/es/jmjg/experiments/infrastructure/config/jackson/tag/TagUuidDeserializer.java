package es.jmjg.experiments.infrastructure.config.jackson.tag;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.jmjg.experiments.domain.tag.entity.TagUuid;
import java.io.IOException;

public class TagUuidDeserializer extends StdDeserializer<TagUuid> {
  public TagUuidDeserializer() {
    super(TagUuid.class);
  }

  @Override
  public TagUuid deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return TagUuid.fromString(p.getValueAsString());
  }
}
