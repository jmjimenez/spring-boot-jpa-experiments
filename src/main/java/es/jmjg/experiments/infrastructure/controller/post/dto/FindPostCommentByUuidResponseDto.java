package es.jmjg.experiments.infrastructure.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for post comment found by id")
public class FindPostCommentByUuidResponseDto {
  @Schema(description = "identifier for the post comment", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  @Schema(description = "identifier for the post", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID postId;

  @Schema(description = "identifier of the user who created the post comment", example = "123e4567-e89b-12d3-a456-426614174001")
  private UUID userId;

  @Schema(description = "Text of the comment", example = "My First Comment")
  private String comment;

  @Schema(description = "Creation date and time", example = "2025-01-01 00:00:00")
  private String createdAt;
}
