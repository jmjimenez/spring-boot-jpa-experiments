package es.jmjg.experiments.infrastructure.controller.tag.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new tag")
public class SaveTagRequestDto {
  // TODO: UUID should be id
  @NotNull(message = "UUID is required")
  @Schema(description = "Unique identifier for the tag", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid;

  @NotEmpty(message = "Name is required")
  @Schema(description = "Name of the tag", example = "java")
  private String name;
}