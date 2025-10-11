package es.jmjg.experiments.application.post.dto;

import es.jmjg.experiments.domain.post.entity.PostUuid;
import java.util.Objects;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;

public record DeletePostDto(
    @NotNull PostUuid uuid,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public DeletePostDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
