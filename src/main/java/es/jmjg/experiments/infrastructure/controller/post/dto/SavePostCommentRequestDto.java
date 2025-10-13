package es.jmjg.experiments.infrastructure.controller.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new post comment")
public class SavePostCommentRequestDto {
  @NotNull(message = "Identifier is required")
  @Schema(description = "Id for the comment", example = "123e4567-e89b-12d3-a456-426614174000")
  @JsonProperty("comment-id")
  private PostCommentUuid id;

  @NotEmpty(message = "Comment is required")
  @Schema(description = "Text of the comment", example = "My First Comment")
  private String comment;
}
