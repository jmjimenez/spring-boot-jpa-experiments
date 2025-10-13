package es.jmjg.experiments.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.jmjg.experiments.infrastructure.config.jackson.post.PostUuidDeserializer;
import es.jmjg.experiments.infrastructure.config.jackson.post.PostUuidSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
@JsonSerialize(using = PostUuidSerializer.class)
@JsonDeserialize(using = PostUuidDeserializer.class)
@Schema(name = "PostId")
public final class PostUuid {
  @JsonProperty("post-id")
  private UUID uuid;

  public static PostUuid randomUUID() {
    return PostUuid.fromUuid(UUID.randomUUID());
  }

  public static PostUuid fromString(String uuid) {
    return PostUuid.fromUuid(UUID.fromString(uuid));
  }

  public static PostUuid fromUuid(UUID uuid) {
    return new PostUuid(uuid);
  }

  public PostUuid(UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid);
  }

  public PostUuid() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PostUuid postUuid)) return false;
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
