package es.jmjg.experiments.application.post.dto;

import java.util.Objects;
import java.util.UUID;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;

public record DeletePostDto(
    @NotNull UUID uuid,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public DeletePostDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
