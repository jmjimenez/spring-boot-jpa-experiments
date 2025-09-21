package es.jmjg.experiments.infrastructure.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new user")
public class UpdateUserRequestDto {
  @NotEmpty(message = "Name is required")
  @Schema(description = "Name of the user", example = "John Doe")
  private String name;

  @NotEmpty(message = "Email is required")
  @Schema(description = "Email address of the user", example = "john.doe@example.com")
  private String email;
}
