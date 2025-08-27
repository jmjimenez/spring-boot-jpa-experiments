package es.jmjg.experiments.application.post.dto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import jakarta.validation.constraints.NotNull;

public record UpdatePostDto(
    @NotNull UUID uuid,
    @NotNull String title,
    @NotNull String body,
    @NotNull List<String> tagNames,
    @NotNull JwtUserDetails userDetails) {

  public UpdatePostDto {
    Objects.requireNonNull(uuid, "uuid cannot be null");
    Objects.requireNonNull(title, "title cannot be null");
    Objects.requireNonNull(body, "body cannot be null");
    Objects.requireNonNull(tagNames, "tagNames cannot be null");
    Objects.requireNonNull(userDetails, "userDetails cannot be null");
  }
}
