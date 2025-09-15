package es.jmjg.experiments.application.post.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public record DeletePostCommentDto(
    @NotNull UUID uuid,
    @NotNull UUID postUuid,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public DeletePostCommentDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(postUuid, "post uuid cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
