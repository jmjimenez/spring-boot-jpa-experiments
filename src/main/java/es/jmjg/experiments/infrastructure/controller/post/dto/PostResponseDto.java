package es.jmjg.experiments.infrastructure.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
@Schema(description = "Response DTO for post in paginated list")
public class PostResponseDto {
  @Schema(description = "Identifier for the post", example = "123e4567-e89b-12d3-a456-426614174000")
  private final UUID id;

  @Schema(description = "Identifier of the user who created the post", example = "123e4567-e89b-12d3-a456-426614174001")
  private final UUID userId;

  @Schema(description = "Title of the post", example = "My First Post")
  private final String title;

  @Schema(description = "Content body of the post", example = "This is the content of my first post.")
  private final String body;

  @Schema(description = "List of tags associated with the post")
  private final List<PostTagResponseDto> tags;

  @Schema(description = "List of identifiers of comments associated with the post")
  private final List<PostCommentResponseDto> postComments;

  public PostResponseDto(UUID id, UUID userId, String title, String body,
      List<PostTagResponseDto> tags, List<PostCommentResponseDto> postComments) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.body = body;
    this.tags = tags != null ? tags : List.of();
    this.postComments = postComments != null ? postComments : List.of();
  }
}
