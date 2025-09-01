package es.jmjg.experiments.application.user.dto;

import java.util.Objects;
import java.util.UUID;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import jakarta.validation.constraints.NotNull;

public record UpdateUserDto(
    @NotNull UUID uuid,
    @NotNull String name,
    @NotNull String email,
    @NotNull JwtUserDetails userDetails) {

  public UpdateUserDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(name, "name cannot be null");
    Objects.requireNonNull(email, "email cannot be null");
    Objects.requireNonNull(userDetails, "userDetails cannot be null");
  }
}
