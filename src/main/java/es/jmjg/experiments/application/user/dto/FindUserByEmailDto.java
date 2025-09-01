package es.jmjg.experiments.application.user.dto;

import java.util.Objects;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import jakarta.validation.constraints.NotNull;

public record FindUserByEmailDto(
    @NotNull String email,
    @NotNull JwtUserDetails userDetails) {

  public FindUserByEmailDto {
    Objects.requireNonNull(email, "email cannot be null");
    Objects.requireNonNull(userDetails, "userDetails cannot be null");
  }
}
