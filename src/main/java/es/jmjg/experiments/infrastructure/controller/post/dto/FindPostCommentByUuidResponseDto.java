package es.jmjg.experiments.infrastructure.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO for post comment found by id")
public class FindPostCommentByUuidResponseDto extends  AbstractPostCommentResponseDto {
  public FindPostCommentByUuidResponseDto(UUID id, UUID userId, UUID postId, String comment, LocalDateTime createdAt) {
    super(id, userId, postId, comment, createdAt);
  }
}
