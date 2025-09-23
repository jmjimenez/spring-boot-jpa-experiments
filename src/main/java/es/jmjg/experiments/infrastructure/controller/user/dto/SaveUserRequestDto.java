package es.jmjg.experiments.infrastructure.controller.user.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new user")
public class SaveUserRequestDto {
  @NotNull(message = "Identifiers is required")
  @Schema(description = "Identifier for the user", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  @NotEmpty(message = "Name is required")
  @Schema(description = "Name of the user", example = "John Doe")
  private String name;

  @NotEmpty(message = "Email is required")
  @Schema(description = "Email address of the user", example = "john.doe@example.com")
  private String email;

  @Schema(description = "Username of the user", example = "johndoe")
  private String username;

  @NotEmpty(message = "Password is required")
  @Schema(description = "Password of the user", example = "securePassword123")
  private String password;
}
