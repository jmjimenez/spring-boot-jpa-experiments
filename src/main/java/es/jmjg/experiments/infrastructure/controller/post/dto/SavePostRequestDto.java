package es.jmjg.experiments.infrastructure.controller.post.dto;

import java.util.List;
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
@Schema(description = "Request DTO for creating a new post")
public class SavePostRequestDto {
  @NotNull(message = "UUID is required")
  @Schema(description = "Unique identifier for the post", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid;

  @NotNull(message = "User ID is required")
  @Schema(description = "UUID of the user creating the post", example = "123e4567-e89b-12d3-a456-426614174001")
  private UUID userId;

  @NotEmpty(message = "Title is required")
  @Schema(description = "Title of the post", example = "My First Post")
  private String title;

  @NotEmpty(message = "Body is required")
  @Schema(description = "Content body of the post", example = "This is the content of my first post.")
  private String body;

  @Schema(description = "List of tag names to associate with the post", example = "[\"java\", \"spring-boot\"]")
  private List<String> tagNames;
}
