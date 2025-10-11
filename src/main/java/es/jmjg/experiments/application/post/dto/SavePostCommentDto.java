package es.jmjg.experiments.application.post.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public record SavePostCommentDto(
    @NotNull PostUuid postUuid,
    @NotNull UUID uuid,
    @NotNull String comment,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public SavePostCommentDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(postUuid, "post id cannot be null");
    Objects.requireNonNull(comment, "comment cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
