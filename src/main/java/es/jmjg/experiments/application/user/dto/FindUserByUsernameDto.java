package es.jmjg.experiments.application.user.dto;

import java.util.Objects;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;

public record FindUserByUsernameDto(
    @NotNull String username,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public FindUserByUsernameDto {
    Objects.requireNonNull(username, "username cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
