package es.jmjg.experiments.application.user.dto;

import java.util.Objects;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import jakarta.validation.constraints.NotNull;

public record FindUserByUsernameDto(
    @NotNull String username,
    @NotNull JwtUserDetails userDetails) {

  public FindUserByUsernameDto {
    Objects.requireNonNull(username, "username cannot be null");
    Objects.requireNonNull(userDetails, "userDetails cannot be null");
  }
}
