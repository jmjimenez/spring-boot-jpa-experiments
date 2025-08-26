package es.jmjg.experiments.application.user.dto;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;

public record FindUserByEmailDto(
    String email,
    JwtUserDetails userDetails) {
}
