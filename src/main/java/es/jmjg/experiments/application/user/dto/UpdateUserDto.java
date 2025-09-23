package es.jmjg.experiments.application.user.dto;

import java.util.Objects;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record UpdateUserDto(
    @NotNull UUID uuid,
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
