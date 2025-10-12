package es.jmjg.experiments.domain.post.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
public final class PostUuid {
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
