package es.jmjg.experiments.infrastructure.controller.tag.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Request DTO for updating a tag")
public class UpdateTagRequestDto {
  @NotNull(message = "Identifier is required")
  @Schema(description = "Identifier for the tag", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid;

  @NotEmpty(message = "Name is required")
  @Schema(description = "New name for the tag", example = "spring-boot")
  private String name;
}
