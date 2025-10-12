package es.jmjg.experiments.infrastructure.controller.tag.dto;

import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.tag.entity.TagUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for tag found by UUID")
public class FindTagByUuidResponseDto extends AbstractTagResponseDto {
  public FindTagByUuidResponseDto(TagUuid uuid, String name, List<PostUuid> posts, List<UserUuid> users) {
    super(uuid, name, posts, users);
  }
}
