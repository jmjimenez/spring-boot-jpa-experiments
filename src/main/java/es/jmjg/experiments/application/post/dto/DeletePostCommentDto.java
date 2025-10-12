package es.jmjg.experiments.application.post.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public record DeletePostCommentDto(
    @NotNull UUID uuid,
    @NotNull PostUuid postUuid,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public DeletePostCommentDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(postUuid, "post id cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
