package es.jmjg.experiments.infrastructure.controller.post.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
@Getter
@Schema(description = "Response DTO for created post")
public class SavePostResponseDto {
  @Schema(description = "Identifier for the post", example = "123e4567-e89b-12d3-a456-426614174000")
  private final UUID uuid;

  @Schema(description = "Identifier of the user who created the post", example = "123e4567-e89b-12d3-a456-426614174001")
  private final UUID userId;

  @Schema(description = "Title of the post", example = "My First Post")
  private final String title;

  @Schema(description = "Content body of the post", example = "This is the content of my first post.")
  private final String body;

  @Schema(description = "List of tags associated with the post")
  private final List<PostTagResponseDto> tags;

  public SavePostResponseDto(UUID uuid, UUID userId, String title, String body,
      List<PostTagResponseDto> tags) {
    this.uuid = uuid;
    this.userId = userId;
    this.title = title;
    this.body = body;
    this.tags = tags != null ? tags : List.of();
  }
}
