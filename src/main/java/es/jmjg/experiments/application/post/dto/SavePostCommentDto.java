package es.jmjg.experiments.application.post.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public record SavePostCommentDto(
    @NotNull UUID uuid,
    @NotNull UUID postUuid,
    @NotNull String comment,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public SavePostCommentDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(postUuid, "postUuid cannot be null");
    Objects.requireNonNull(uuid, "commentcannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
