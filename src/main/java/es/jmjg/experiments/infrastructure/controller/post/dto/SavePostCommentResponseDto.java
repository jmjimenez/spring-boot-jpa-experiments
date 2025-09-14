package es.jmjg.experiments.infrastructure.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for created post")
public class SavePostCommentResponseDto {
  @Schema(description = "Unique identifier for the comment", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  @Schema(description = "Identifier of the user who created the comment", example = "123e4567-e89b-12d3-a456-426614174001")
  private UUID userId;

  @Schema(description = "Identifier of the post to comment", example = "123e4567-e89b-12d3-a456-426614174001")
  private UUID postId;

  @Schema(description = "Text of the comment", example = "My First Comment")
  private String comment;
}
