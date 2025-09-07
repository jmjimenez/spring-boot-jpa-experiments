package es.jmjg.experiments.application.post.dto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;

public record SavePostDto(
    @NotNull UUID uuid,
    @NotNull String title,
    @NotNull String body,
    @NotNull AuthenticatedUserDto authenticatedUser,
    List<String> tagNames) {

  public SavePostDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(title, "title cannot be null");
    Objects.requireNonNull(body, "body cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
