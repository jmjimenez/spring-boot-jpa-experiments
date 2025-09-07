package es.jmjg.experiments.application.tag.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public record SaveTagDto(
  @NotNull
  UUID uuid,
  @NotNull
  String tagName,
  @NotNull
  AuthenticatedUserDto authenticatedUser) {

  public SaveTagDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(uuid, "tag name cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
