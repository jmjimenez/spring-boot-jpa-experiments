package es.jmjg.experiments.application.user.dto;

import java.util.Objects;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;

public record FindUserByEmailDto(
    @NotNull String email,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public FindUserByEmailDto {
    Objects.requireNonNull(email, "email cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
