package es.jmjg.experiments.application.user.dto;

import java.util.Objects;
import java.util.UUID;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;

public record SaveUserDto(
    @NotNull UUID uuid,
    @NotNull String name,
    @NotNull String email,
    @NotNull String username,
    @NotNull String password,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public SaveUserDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(name, "name cannot be null");
    Objects.requireNonNull(email, "email cannot be null");
    Objects.requireNonNull(username, "username cannot be null");
    Objects.requireNonNull(password, "password cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
