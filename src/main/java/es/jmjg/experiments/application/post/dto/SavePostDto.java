package es.jmjg.experiments.application.post.dto;

import java.util.List;
import java.util.UUID;

public record SavePostDto(
    UUID uuid,
    String title,
    String body,
    UUID userUuid,
    List<String> tagNames) {
}
