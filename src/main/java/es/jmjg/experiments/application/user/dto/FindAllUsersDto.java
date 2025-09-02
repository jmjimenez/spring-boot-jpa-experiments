package es.jmjg.experiments.application.user.dto;

import java.util.Objects;

import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;

public record FindAllUsersDto(
    @NotNull Pageable pageable,
    @NotNull AuthenticatedUserDto authenticatedUser) {

  public FindAllUsersDto {
    Objects.requireNonNull(pageable, "pageable cannot be null");
    Objects.requireNonNull(authenticatedUser, "authenticatedUser cannot be null");
  }
}
