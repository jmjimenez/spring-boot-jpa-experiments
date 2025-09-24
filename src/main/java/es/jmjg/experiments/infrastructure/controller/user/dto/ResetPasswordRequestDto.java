package es.jmjg.experiments.infrastructure.controller.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Request DTO to reset password")
public class ResetPasswordRequestDto {
  @Schema(description = "Reset key", example = "reset-key")
  @JsonProperty("reset-key")
  private String resetKey;

  @Schema(description = "New password", example = "new-password")
  @JsonProperty("new-password")
  private String newPassword;
}
