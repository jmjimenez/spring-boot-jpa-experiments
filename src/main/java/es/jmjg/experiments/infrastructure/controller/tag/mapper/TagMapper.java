package es.jmjg.experiments.infrastructure.controller.tag.mapper;

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
        (uuid, name, posts, users) -> new SaveTagResponseDto(uuid, name, posts, users));
  }

  public SaveTagResponseDto toSaveResponseDtoWithRelations(Tag tag, List<Post> posts, List<User> users) {
    return mapToResponseDto(tag, posts, users,
        (uuid, name, postsUuids, userUuids) -> new SaveTagResponseDto(uuid, name, postsUuids, userUuids));
  }

  public UpdateTagResponseDto toUpdateTagResponseDto(Tag updatedTag) {
    return mapToResponseDto(updatedTag, List.of(), List.of(),
        (uuid, name, posts, users) -> new UpdateTagResponseDto(uuid, name, posts, users));
  }

  public UpdateTagResponseDto toUpdateResponseDtoWithRelations(Tag tag, List<Post> posts, List<User> users) {
    return mapToResponseDto(tag, posts, users,
        (uuid, name, postsUuids, userUuids) -> new UpdateTagResponseDto(uuid, name, postsUuids, userUuids));
  }

  public Tag toDomain(SaveTagRequestDto tagRequestDto) {
    if (tagRequestDto == null) {
      return null;
    }
    Tag tag = new Tag();
    tag.setUuid(tagRequestDto.getUuid());
    tag.setName(tagRequestDto.getName());
    return tag;
  }

  public FindTagByPatternResponseDto toFindByPatternResponseDto(Tag tag, List<Post> posts, List<User> users) {
    return mapToResponseDto(tag, posts, users,
        (uuid, name, postsUuids, userUuids) -> new FindTagByPatternResponseDto(uuid, name, postsUuids, userUuids));
  }

  public FindTagByUuidResponseDto toFindByUuidResponseDto(Tag tag, List<Post> posts, List<User> users) {
    return mapToResponseDto(tag, posts, users,
        (uuid, name, postsUuids, userUuids) -> new FindTagByUuidResponseDto(uuid, name, postsUuids, userUuids));
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