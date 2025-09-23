package es.jmjg.experiments.infrastructure.controller.user.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Response DTO for created user")
public class SaveUserResponseDto {
  @Schema(description = "Identifier for the user", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  @Schema(description = "Name of the user", example = "John Doe")
  private String name;

  @Schema(description = "Email address of the user", example = "john.doe@example.com")
  private String email;

  @Schema(description = "Username of the user", example = "johndoe")
  private String username;
}
