package es.jmjg.experiments.infrastructure.controller.post.dto;

import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Response DTO for created post comment")
public class SavePostCommentResponseDto extends AbstractPostCommentResponseDto {
  public SavePostCommentResponseDto(
      PostCommentUuid id,
      UserUuid userId,
      PostUuid postId,
      String comment,
      LocalDateTime createdAt
  ) {
    super(id, userId, postId, comment,  createdAt);
  }
}
