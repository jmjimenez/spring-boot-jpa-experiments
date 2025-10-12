package es.jmjg.experiments.application.post.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public record DeletePostCommentDto(
    @NotNull PostCommentUuid uuid,
    @NotNull PostUuid postUuid,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public DeletePostCommentDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(postUuid, "post id cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
