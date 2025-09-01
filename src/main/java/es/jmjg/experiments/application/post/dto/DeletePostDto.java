package es.jmjg.experiments.application.post.dto;

import java.util.Objects;
import java.util.UUID;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import jakarta.validation.constraints.NotNull;

public record DeletePostDto(
    @NotNull UUID uuid,
    @NotNull JwtUserDetails userDetails) {

  public DeletePostDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(userDetails, "userDetails cannot be null");
  }
}
