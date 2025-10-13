package es.jmjg.experiments.infrastructure.controller.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
abstract public class AbstractUserResponseDto {
  @Schema(description = "Id for the user", example = "123e4567-e89b-12d3-a456-426614174000")
  @JsonProperty("user-id")
  private UserUuid id;

  @Schema(description = "Name of the user", example = "John Doe")
  private String name;

  @Schema(description = "Email address of the user", example = "john.doe@example.com")
  private String email;

  @Schema(description = "Username of the user", example = "johndoe")
  private String username;

  @Schema(description = "List of post identifiers associated with the user")
  private List<PostUuid> posts;

  @Schema(description = "List of tag names associated with the user")
  private List<String> tags;

  @Schema(description = "List of comments posted by the user")
  private List<PostCommentUuid> comments;
}
