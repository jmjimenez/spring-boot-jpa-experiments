package es.jmjg.experiments.infrastructure.controller.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
@Getter
@Schema(description = "Response DTO for updated post")
public class UpdatePostResponseDto {
  @Schema(description = "Id for the post", example = "123e4567-e89b-12d3-a456-426614174000")
  @JsonProperty("post-id")
  private final PostUuid id;

  @Schema(description = "Id of the user who updated the post", example = "123e4567-e89b-12d3-a456-426614174001")
  @JsonProperty("user-id")
  private final UserUuid userId;

  @Schema(description = "Updated title of the post", example = "Updated Post Title")
  private final String title;

  @Schema(description = "Updated content body of the post", example = "This is the updated content of my post.")
  private final String body;

  @Schema(description = "List of tags associated with the post")
  private final List<PostTagResponseDto> tags;

  // Constructor with tags
  public UpdatePostResponseDto(PostUuid id, UserUuid userId, String title, String body,
      List<PostTagResponseDto> tags) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.body = body;
    this.tags = tags != null ? tags : List.of();
  }
}
