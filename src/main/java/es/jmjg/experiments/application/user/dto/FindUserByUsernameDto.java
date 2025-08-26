package es.jmjg.experiments.application.user.dto;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;

public record FindUserByUsernameDto(
    String username,
    JwtUserDetails userDetails) {
}
