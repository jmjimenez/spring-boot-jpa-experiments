package es.jmjg.experiments.infrastructure.controller.post.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
@Getter
@Schema(description = "Response DTO for updated post")
public class UpdatePostResponseDto {
  @Schema(description = "Identifier for the post", example = "123e4567-e89b-12d3-a456-426614174000")
  private final UUID id;

  @Schema(description = "Identifier of the user who updated the post", example = "123e4567-e89b-12d3-a456-426614174001")
  private final UUID userId;

  @Schema(description = "Updated title of the post", example = "Updated Post Title")
  private final String title;

  @Schema(description = "Updated content body of the post", example = "This is the updated content of my post.")
  private final String body;

  @Schema(description = "List of tags associated with the post")
  private final List<PostTagResponseDto> tags;

  // Constructor with tags
  public UpdatePostResponseDto(UUID id, UUID userId, String title, String body,
      List<PostTagResponseDto> tags) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.body = body;
    this.tags = tags != null ? tags : List.of();
  }
}
