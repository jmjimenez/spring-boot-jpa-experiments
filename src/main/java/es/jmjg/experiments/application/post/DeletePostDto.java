package es.jmjg.experiments.application.post;

import java.util.UUID;

import es.jmjg.experiments.infrastructure.security.JwtUserDetails;

public record DeletePostDto(
    UUID uuid,
    JwtUserDetails userDetails) {
}
