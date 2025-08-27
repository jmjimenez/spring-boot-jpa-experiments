package es.jmjg.experiments.application.user.dto;

import java.util.Objects;

import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import jakarta.validation.constraints.NotNull;

public record FindAllUsersDto(
    @NotNull Pageable pageable,
    @NotNull JwtUserDetails userDetails) {

  public FindAllUsersDto {
    Objects.requireNonNull(pageable, "pageable cannot be null");
    Objects.requireNonNull(userDetails, "userDetails cannot be null");
  }
}
