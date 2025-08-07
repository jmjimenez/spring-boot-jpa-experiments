package es.jmjg.experiments.infrastructure.controller.post.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for post tag")
public class PostTagResponseDto {
  @Schema(description = "Unique identifier for the tag", example = "123e4567-e89b-12d3-a456-426614174002")
  private UUID uuid;

  @Schema(description = "Name of the tag", example = "java")
  private String name;
}
