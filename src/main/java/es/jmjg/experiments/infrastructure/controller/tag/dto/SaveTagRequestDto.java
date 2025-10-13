package es.jmjg.experiments.infrastructure.controller.tag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.jmjg.experiments.domain.tag.entity.TagUuid;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new tag")
public class SaveTagRequestDto {
  @NotNull(message = "Identifier is required")
  @Schema(description = "Id for the tag", example = "123e4567-e89b-12d3-a456-426614174000")
  @JsonProperty("tag-id")
  private TagUuid uuid;

  @NotEmpty(message = "Name is required")
  @Schema(description = "Name of the tag", example = "java")
  private String name;
}
