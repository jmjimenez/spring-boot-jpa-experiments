package es.jmjg.experiments.application.post.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public record SavePostCommentDto(
    @NotNull UUID id,
    @NotNull UUID postId,
    @NotNull String comment,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public SavePostCommentDto {
    Objects.requireNonNull(id, "id cannot be null");
    Objects.requireNonNull(postId, "post id cannot be null");
    Objects.requireNonNull(id, "comment cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
