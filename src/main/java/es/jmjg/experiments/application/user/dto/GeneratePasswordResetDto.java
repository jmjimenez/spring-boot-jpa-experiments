package es.jmjg.experiments.application.user.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public record GeneratePasswordResetDto(
  @NotNull String username,
  @NotNull String email) {

  public GeneratePasswordResetDto {
    Objects.requireNonNull(username, "username cannot be null");
    Objects.requireNonNull(email, "email cannot be null");
  }
}
