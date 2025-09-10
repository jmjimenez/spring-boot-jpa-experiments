package es.jmjg.experiments.application.user.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public record ResetPasswordDto(
  @NotNull String username,
  @NotNull String email,
  @NotNull String resetKey,
  @NotNull String newPassword
) {

  public ResetPasswordDto {
    Objects.requireNonNull(username, "username cannot be null");
    Objects.requireNonNull(email, "email cannot be null");
    Objects.requireNonNull(resetKey, "resetKey cannot be null");
    Objects.requireNonNull(newPassword, "newPassword cannot be null");
  }
}
