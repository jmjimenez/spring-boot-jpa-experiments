package es.jmjg.experiments.application.user.dto;

import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;

public record FindAllUsersDto(
    Pageable pageable,
    JwtUserDetails userDetails) {
}
