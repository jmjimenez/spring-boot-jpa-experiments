package es.jmjg.experiments.infrastructure.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response DTO")
public class FindPostByTagResponseDto extends PostResponseDto {
  public FindPostByTagResponseDto(UUID uuid, UUID userId, String title, String body,
    List<PostTagResponseDto> tags, List<PostCommentResponseDto> postComments) {
    super(uuid, userId, title, body, tags, postComments);
  }
}
