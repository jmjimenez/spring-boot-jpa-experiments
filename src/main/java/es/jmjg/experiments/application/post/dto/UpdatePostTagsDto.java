package es.jmjg.experiments.application.post.dto;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdatePostTagsDto(
    @NotNull PostUuid postUuid,
    @NotNull List<@NotNull String> tagNames,
    @NotNull AuthenticatedUserDto authenticatedUserDto
) {}
