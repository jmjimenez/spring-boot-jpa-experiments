package es.jmjg.experiments.infrastructure.controller.tag.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for tag found by pattern")
public class FindTagByPatternResponseDto extends TagResponseDto {
  public FindTagByPatternResponseDto(UUID uuid, String name, List<UUID> posts, List<UUID> users) {
    super(uuid, name, posts, users);
  }
}
