package es.jmjg.experiments.application.user.dto;

import java.util.Objects;
import java.util.UUID;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import jakarta.validation.constraints.NotNull;

public record DeleteUserDto(
    @NotNull UUID uuid,
    @NotNull JwtUserDetails userDetails) {

  public DeleteUserDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(userDetails, "userDetails cannot be null");
  }
}
