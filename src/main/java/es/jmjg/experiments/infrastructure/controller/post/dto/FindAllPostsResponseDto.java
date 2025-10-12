package es.jmjg.experiments.infrastructure.controller.post.dto;

import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response DTO for post in paginated list")
public class FindAllPostsResponseDto extends AbstractPostResponseDto {
  public FindAllPostsResponseDto(PostUuid id, UserUuid userId, String title, String body,
    List<PostTagResponseDto> tags, List<PostCommentResponseDto> postComments) {
    super(id, userId, title, body, tags, postComments);
  }
}
