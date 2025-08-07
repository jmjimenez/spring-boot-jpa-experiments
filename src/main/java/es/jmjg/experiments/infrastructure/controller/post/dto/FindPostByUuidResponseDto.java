package es.jmjg.experiments.infrastructure.controller.post.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Response DTO for post found by UUID")
public class FindPostByUuidResponseDto {
  @Schema(description = "Unique identifier for the post", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid;

  @Schema(description = "UUID of the user who created the post", example = "123e4567-e89b-12d3-a456-426614174001")
  private UUID userId;

  @Schema(description = "Title of the post", example = "My First Post")
  private String title;

  @Schema(description = "Content body of the post", example = "This is the content of my first post.")
  private String body;

  @Schema(description = "List of tags associated with the post")
  private List<PostTagResponseDto> tags;

  // Constructor with tags
  public FindPostByUuidResponseDto(UUID uuid, UUID userId, String title, String body,
      List<PostTagResponseDto> tags) {
    this.uuid = uuid;
    this.userId = userId;
    this.title = title;
    this.body = body;
    this.tags = tags != null ? tags : List.of();
  }
}
