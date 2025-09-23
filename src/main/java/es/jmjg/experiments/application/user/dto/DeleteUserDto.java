package es.jmjg.experiments.application.user.dto;

import java.util.Objects;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record DeleteUserDto(
    @NotNull UUID uuid,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public DeleteUserDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
