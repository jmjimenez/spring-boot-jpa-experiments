package es.jmjg.experiments.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.jmjg.experiments.infrastructure.config.jackson.user.UserUuidDeserializer;
import es.jmjg.experiments.infrastructure.config.jackson.user.UserUuidSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
@JsonSerialize(using = UserUuidSerializer.class)
@JsonDeserialize(using = UserUuidDeserializer.class)
@Schema(name = "UserId")
public class UserUuid {
  @JsonProperty("user-id")
  private UUID uuid;

  public static UserUuid randomUUID() {
    return UserUuid.fromUuid(UUID.randomUUID());
  }

  public static UserUuid fromString(String uuid) {
    return UserUuid.fromUuid(UUID.fromString(uuid));
  }

  public static UserUuid fromUuid(UUID uuid) {
    return new UserUuid(uuid);
  }

  public UserUuid(UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid);
  }

  public UserUuid() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserUuid userUuid)) return false;
    return uuid.equals(userUuid.uuid);
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
