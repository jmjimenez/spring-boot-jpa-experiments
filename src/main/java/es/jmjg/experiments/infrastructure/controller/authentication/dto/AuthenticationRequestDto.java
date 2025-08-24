package es.jmjg.experiments.infrastructure.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Authentication request containing user credentials")
public class AuthenticationRequestDto {
  @NotNull
  @Size(max = 255)
  @Schema(description = "Username or email for authentication", example = "leanne_graham", required = true)
  private String login;

  @NotNull
  @Size(max = 255)
  @Schema(description = "User password", example = "password", required = true)
  private String password;
}
