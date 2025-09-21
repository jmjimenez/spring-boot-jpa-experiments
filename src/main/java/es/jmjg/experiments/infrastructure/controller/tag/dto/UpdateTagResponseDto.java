package es.jmjg.experiments.infrastructure.controller.tag.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Response DTO for updated tag")
public class UpdateTagResponseDto {
  @Schema(description = "Unique identifier for the tag", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid;

  @Schema(description = "Updated name of the tag", example = "spring-boot")
  private String name;

  @Schema(description = "List of post UUIDs associated with this tag")
  private List<UUID> posts;

  @Schema(description = "List of user UUIDs associated with this tag")
  private List<UUID> users;
}
