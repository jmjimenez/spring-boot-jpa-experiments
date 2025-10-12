package es.jmjg.experiments.application.tag.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.domain.tag.entity.TagUuid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public record SaveTagDto(
  @NotNull
  TagUuid uuid,
  @NotNull
  String tagName,
  @NotNull
  AuthenticatedUserDto authenticatedUser) {

  public SaveTagDto {
    Objects.requireNonNull(uuid, "id cannot be null");
    Objects.requireNonNull(uuid, "tag name cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
  }
}
