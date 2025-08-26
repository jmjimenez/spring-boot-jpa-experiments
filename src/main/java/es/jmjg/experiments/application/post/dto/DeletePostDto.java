package es.jmjg.experiments.application.post.dto;

import java.util.UUID;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;

public record DeletePostDto(
    UUID uuid,
    JwtUserDetails userDetails) {
}
