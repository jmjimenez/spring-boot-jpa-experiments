package es.jmjg.experiments.application.tag.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.domain.tag.entity.TagUuid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public record DeleteTagDto(
  @NotNull
  TagUuid uuid,
  @NotNull
  AuthenticatedUserDto authenticatedUser) {

  public DeleteTagDto {
      Objects.requireNonNull(uuid, "id cannot be null");
      Objects.requireNonNull(authenticatedUser, "authenticatedUserDto cannot be null");
    }
}
