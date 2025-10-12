package es.jmjg.experiments.infrastructure.controller.post.dto;

import es.jmjg.experiments.domain.tag.entity.TagUuid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagDto {
  @NotEmpty(message = "Identifier is required")
  @Schema(description = "Identifier of the tag", example = "123e4567-e89b-12d3-a456-426614174001")
  private TagUuid id;

  @NotEmpty(message = "Name is required")
  @Schema(description = "Name of the tag", example = "java")
  private String name;
}
