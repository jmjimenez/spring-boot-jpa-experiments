package es.jmjg.experiments.infrastructure.controller.tag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.tag.entity.TagUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
abstract public class AbstractTagResponseDto {
  @Schema(description = "Id for the tag", example = "123e4567-e89b-12d3-a456-426614174000")
  @JsonProperty("tag-id")
  private TagUuid id;

  @Schema(description = "Updated name of the tag", example = "spring-boot")
  private String name;

  @Schema(description = "List of post UUIDs associated with this tag")
  private List<PostUuid> posts;

  @Schema(description = "List of user UUIDs associated with this tag")
  private List<UserUuid> users;
}
