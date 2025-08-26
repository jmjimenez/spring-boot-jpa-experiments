package es.jmjg.experiments.application.user.dto;

import java.util.UUID;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;

public record UpdateUserDto(
    Integer id,
    UUID uuid,
    String name,
    String email,
    String username,
    String password,
    JwtUserDetails userDetails) {
}
