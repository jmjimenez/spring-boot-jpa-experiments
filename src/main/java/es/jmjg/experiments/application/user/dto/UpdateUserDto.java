package es.jmjg.experiments.application.user.dto;

import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;

public record UpdateUserDto(
    @NotNull UserUuid uuid,
    @NotNull String name,
    @NotNull String email,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public UpdateUserDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(name, "name cannot be null");
    Objects.requireNonNull(email, "email cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
