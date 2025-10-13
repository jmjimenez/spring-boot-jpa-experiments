package es.jmjg.experiments.infrastructure.controller.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Response DTO for post comment")
public class PostCommentResponseDto {
  @Schema(description = "Id for the comment", example = "123e4567-e89b-12d3-a456-426614174002")
  @JsonProperty("comment-id")
  private PostCommentUuid id;
}
