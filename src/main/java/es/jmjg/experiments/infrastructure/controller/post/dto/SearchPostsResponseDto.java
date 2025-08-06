package es.jmjg.experiments.infrastructure.controller.post.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchPostsResponseDto {
  private UUID uuid;
  private UUID userId;
  private String title;
  private String body;
  private List<PostTagResponseDto> tags;

  // Constructor with tags
  public SearchPostsResponseDto(UUID uuid, UUID userId, String title, String body,
      List<PostTagResponseDto> tags) {
    this.uuid = uuid;
    this.userId = userId;
    this.title = title;
    this.body = body;
    this.tags = tags != null ? tags : List.of();
  }
}
