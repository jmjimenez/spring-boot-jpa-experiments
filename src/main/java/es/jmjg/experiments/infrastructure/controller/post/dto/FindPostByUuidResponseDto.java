package es.jmjg.experiments.infrastructure.controller.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response DTO")
public class FindPostByUuidResponseDto extends PostResponseDto {
  public FindPostByUuidResponseDto(UUID id, UUID userId, String title, String body,
    List<PostTagResponseDto> tags, List<PostCommentResponseDto> postComments) {
    super(id, userId, title, body, tags, postComments);
  }
}
