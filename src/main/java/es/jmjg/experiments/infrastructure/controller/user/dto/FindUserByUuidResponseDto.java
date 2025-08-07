package es.jmjg.experiments.infrastructure.controller.user.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for user found by UUID")
public class FindUserByUuidResponseDto {
  @Schema(description = "Unique identifier for the user", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid;

  @Schema(description = "Name of the user", example = "John Doe")
  private String name;

  @Schema(description = "Email address of the user", example = "john.doe@example.com")
  private String email;

  @Schema(description = "Username of the user", example = "johndoe")
  private String username;

  @Schema(description = "List of post UUIDs associated with the user")
  private List<UUID> posts;

  @Schema(description = "List of tag names associated with the user")
  private List<String> tags;
}
