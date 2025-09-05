package es.jmjg.experiments.infrastructure.controller.post.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostTagsResponseDto {

  @NotEmpty(message = "UUID is required")
  @Schema(description = "UUID of the post", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid;

  @NotEmpty(message = "Title is required")
  @Schema(description = "Title of the post", example = "My Post Title")
  private String title;

  @NotEmpty(message = "Tags are required")
  @Schema(description = "List of tags associated with the post")
  private List<TagDto> tags;
}
