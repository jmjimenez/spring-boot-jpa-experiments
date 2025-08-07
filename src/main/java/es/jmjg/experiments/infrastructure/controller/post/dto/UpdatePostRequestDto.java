package es.jmjg.experiments.infrastructure.controller.post.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for updating an existing post")
public class UpdatePostRequestDto {
  @NotEmpty(message = "Title is required")
  @Schema(description = "Updated title of the post", example = "Updated Post Title")
  private String title;

  @NotEmpty(message = "Body is required")
  @Schema(description = "Updated content body of the post", example = "This is the updated content of my post.")
  private String body;

  @Schema(description = "List of tag names to associate with the post", example = "[\"java\", \"spring-boot\", \"updated\"]")
  private List<String> tagNames;
}
