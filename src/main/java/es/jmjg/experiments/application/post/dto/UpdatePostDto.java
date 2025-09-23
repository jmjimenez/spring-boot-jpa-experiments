package es.jmjg.experiments.application.post.dto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;

public record UpdatePostDto(
    @NotNull UUID uuid,
    @NotNull String title,
    @NotNull String body,
    List<String> tagNames,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public UpdatePostDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(title, "title cannot be null");
    Objects.requireNonNull(body, "body cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
