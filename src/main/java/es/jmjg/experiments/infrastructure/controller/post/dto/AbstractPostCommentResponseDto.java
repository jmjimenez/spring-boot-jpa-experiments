package es.jmjg.experiments.infrastructure.controller.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractPostCommentResponseDto {
  @Schema(description = "Identifier for the comment", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  @Schema(description = "Identifier of the user who created the comment", example = "123e4567-e89b-12d3-a456-426614174001")
  @JsonProperty("user-id")
  private UUID userId;

  @Schema(description = "Identifier of the post to comment", example = "123e4567-e89b-12d3-a456-426614174001")
  @JsonProperty("post-id")
  private UUID postId;

  @Schema(description = "Text of the comment", example = "My First Comment")
  private String comment;

  @Schema(description = "Creation date and time", example = "2025-01-01 00:00:00")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  @JsonProperty("created-time")
  private LocalDateTime createdAt;
}
