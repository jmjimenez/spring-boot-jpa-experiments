package es.jmjg.experiments.application.user.dto;

import java.util.UUID;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;

public record FindUserByUuidDto(
    UUID uuid,
    JwtUserDetails userDetails) {
}
