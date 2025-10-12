package es.jmjg.experiments.domain.tag.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.jmjg.experiments.infrastructure.config.jackson.tag.TagUuidDeserializer;
import es.jmjg.experiments.infrastructure.config.jackson.tag.TagUuidSerializer;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
@JsonSerialize(using = TagUuidSerializer.class)
@JsonDeserialize(using = TagUuidDeserializer.class)
public final class TagUuid {
  private UUID uuid;

  public static TagUuid randomUUID() {
    return TagUuid.fromUuid(UUID.randomUUID());
  }

  public static TagUuid fromString(String uuid) {
    return TagUuid.fromUuid(UUID.fromString(uuid));
  }

  public static TagUuid fromUuid(UUID uuid) {
    return new TagUuid(uuid);
  }

  public TagUuid(UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid);
  }

  public TagUuid() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TagUuid postUuid)) return false;
    return uuid.equals(postUuid.uuid);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public String toString() {
    return uuid.toString();
  }
}
