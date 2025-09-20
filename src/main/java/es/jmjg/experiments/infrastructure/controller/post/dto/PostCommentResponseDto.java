package es.jmjg.experiments.infrastructure.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for post comment")
public class PostCommentResponseDto {
  @Schema(description = "Unique identifier for the comment", example = "123e4567-e89b-12d3-a456-426614174002")
  private UUID uuid;
}
