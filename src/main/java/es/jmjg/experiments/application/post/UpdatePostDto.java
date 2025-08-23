package es.jmjg.experiments.application.post;

import java.util.List;
import java.util.UUID;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;

public record UpdatePostDto(
    UUID uuid,
    String title,
    String body,
    List<String> tagNames,
    JwtUserDetails userDetails) {
}
