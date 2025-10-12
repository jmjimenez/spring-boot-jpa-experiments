package es.jmjg.experiments.infrastructure.controller.post.dto;

import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Response DTO for post comment found by id")
public class FindPostCommentByUuidResponseDto extends  AbstractPostCommentResponseDto {
  public FindPostCommentByUuidResponseDto(PostCommentUuid id, UserUuid userId, PostUuid postId, String comment, LocalDateTime createdAt) {
    super(id, userId, postId, comment, createdAt);
  }
}
