package es.jmjg.experiments.application.user.dto;

import java.time.LocalDateTime;

public record PasswordResetDto(
  String username,
  String email,
  LocalDateTime expiryDate
) {

}
