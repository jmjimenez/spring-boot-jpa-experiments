package es.jmjg.experiments.infrastructure.controller.user.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for user in paginated list")
public class FindAllUsersResponseDto extends AbstractUserResponseDto {
  public FindAllUsersResponseDto(UUID id, String name, String email, String username, List<UUID> posts, List<String> tags) {
    super(id, name, email, username, posts, tags);
  }
}
