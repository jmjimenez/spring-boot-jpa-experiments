package es.jmjg.experiments.application.post.dto;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record UpdatePostTagsDto(
    @NotNull UUID postUuid,
    @NotNull List<@NotNull String> tagNames,
    @NotNull AuthenticatedUserDto authenticatedUserDto
) {}
