package es.jmjg.experiments.infrastructure.controller.tag.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for created tag")
public class SaveTagResponseDto extends AbstractTagResponseDto {
  public SaveTagResponseDto(UUID uuid, String name, List<UUID> posts, List<UUID> users) {
    super(uuid, name, posts, users);
  }
}
