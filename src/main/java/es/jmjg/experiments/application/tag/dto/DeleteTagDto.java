package es.jmjg.experiments.application.tag.dto;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public record DeleteTagDto(
  @NotNull
  UUID uuid,
  @NotNull
  AuthenticatedUserDto authenticatedUser) {

  public DeleteTagDto {
      Objects.requireNonNull(uuid, "uuid cannot be null");
      Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
    }
}
