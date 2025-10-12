package es.jmjg.experiments.infrastructure.controller.user.dto;

import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for user found by UUID")
public class FindUserByUuidResponseDto extends AbstractUserResponseDto {
  public FindUserByUuidResponseDto(UserUuid id, String name, String email, String username, List<PostUuid> posts, List<String> tags, List<PostCommentUuid> comments) {
    super(id, name, email, username, posts, tags, comments);
  }
}
