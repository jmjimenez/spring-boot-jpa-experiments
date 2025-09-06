package es.jmjg.experiments.infrastructure.controller.tag.mapper;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.tag.dto.DeleteTagDto;
import es.jmjg.experiments.application.tag.dto.SaveTagDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByPatternResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagRequestDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagResponseDto;

@Component
public class TagMapper {

  public SaveTagResponseDto toSaveTagResponseDto(Tag tag) {
    return mapToResponseDto(tag, List.of(), List.of(),
      SaveTagResponseDto::new);
  }

  public UpdateTagResponseDto toUpdateTagResponseDto(Tag updatedTag) {
    return mapToResponseDto(updatedTag, List.of(), List.of(),
      UpdateTagResponseDto::new);
  }

  public FindTagByPatternResponseDto toFindByPatternResponseDto(Tag tag, List<Post> posts, List<User> users) {
    return mapToResponseDto(tag, posts, users,
      FindTagByPatternResponseDto::new);
  }

  public FindTagByUuidResponseDto toFindByUuidResponseDto(Tag tag, List<Post> posts, List<User> users) {
    return mapToResponseDto(tag, posts, users,
      FindTagByUuidResponseDto::new);
  }

  public DeleteTagDto toDeleteTagDto(UUID uuid, AuthenticatedUserDto authenticatedUser) {
    return new DeleteTagDto(uuid, authenticatedUser);
  }

  public SaveTagDto toSaveTagDto(@Valid SaveTagRequestDto dto, @NotNull AuthenticatedUserDto authenticatedUser) {
    return new SaveTagDto(
      dto.getUuid(),
      dto.getName(),
      authenticatedUser
    );
  }

  @FunctionalInterface
  private interface TagDtoConstructor<T> {
    T create(UUID uuid, String name, List<UUID> posts, List<UUID> users);
  }

  private <T> T mapToResponseDto(Tag tag, List<Post> posts, List<User> users, TagDtoConstructor<T> dtoConstructor) {
    if (tag == null) {
      return null;
    }

    List<UUID> postUuids = posts.stream()
        .map(Post::getUuid)
        .collect(Collectors.toList());

    List<UUID> userUuids = users.stream()
        .map(User::getUuid)
        .collect(Collectors.toList());

    return dtoConstructor.create(tag.getUuid(), tag.getName(), postUuids, userUuids);
  }
}
