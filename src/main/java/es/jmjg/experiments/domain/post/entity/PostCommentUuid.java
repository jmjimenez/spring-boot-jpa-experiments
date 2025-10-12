package es.jmjg.experiments.domain.post.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.jmjg.experiments.infrastructure.config.jackson.post.PostCommentUuidDeserializer;
import es.jmjg.experiments.infrastructure.config.jackson.post.PostCommentUuidSerializer;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
@JsonSerialize(using = PostCommentUuidSerializer.class)
@JsonDeserialize(using = PostCommentUuidDeserializer.class)
public final class PostCommentUuid {
  private UUID uuid;

  public static PostCommentUuid randomUUID() {
    return PostCommentUuid.fromUuid(UUID.randomUUID());
  }

  public static PostCommentUuid fromString(String uuid) {
    return PostCommentUuid.fromUuid(UUID.fromString(uuid));
  }

  public static PostCommentUuid fromUuid(UUID uuid) {
    return new PostCommentUuid(uuid);
  }

  public PostCommentUuid(UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid);
  }

  public PostCommentUuid() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PostCommentUuid postUuid)) return false;
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
