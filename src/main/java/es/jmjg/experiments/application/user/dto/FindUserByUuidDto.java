package es.jmjg.experiments.application.user.dto;

import java.util.Objects;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record FindUserByUuidDto(
    @NotNull UUID uuid,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public FindUserByUuidDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
