package es.jmjg.experiments.application.post.dto;

import java.util.Objects;
import java.util.UUID;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;

public record DeletePostDto(
    @NotNull UUID uuid,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public DeletePostDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
