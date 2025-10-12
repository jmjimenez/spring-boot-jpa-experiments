package es.jmjg.experiments.application.user.dto;

import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;

public record FindUserByUuidDto(
    @NotNull UserUuid uuid,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public FindUserByUuidDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
