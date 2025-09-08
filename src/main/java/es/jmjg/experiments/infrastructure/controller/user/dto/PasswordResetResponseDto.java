package es.jmjg.experiments.infrastructure.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for reset password")
public class PasswordResetResponseDto {
  @Schema(description = "Reset key", example = "some-reset-key")
  private String resetKey;
}
