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
    if (tag == null) {
      return null;
    }
    return toSaveResponseDtoWithRelations(tag, List.of(), List.of());
  }

  public SaveTagResponseDto toSaveResponseDtoWithRelations(Tag tag, List<Post> posts, List<User> users) {
    if (tag == null) {
      return null;
    }
    List<UUID> postUuids = posts.stream()
        .map(Post::getUuid)
        .collect(Collectors.toList());
    List<UUID> userUuids = users.stream()
        .map(User::getUuid)
        .collect(Collectors.toList());
    return new SaveTagResponseDto(tag.getUuid(), tag.getName(), postUuids, userUuids);
  }

  public UpdateTagResponseDto toUpdateTagResponseDto(Tag updatedTag) {
    if (updatedTag == null) {
      return null;
    }
    return toUpdateResponseDtoWithRelations(updatedTag, List.of(), List.of());
  }

  public UpdateTagResponseDto toUpdateResponseDtoWithRelations(Tag tag, List<Post> posts, List<User> users) {
    if (tag == null) {
      return null;
    }
    List<UUID> postUuids = posts.stream()
        .map(Post::getUuid)
        .collect(Collectors.toList());
    List<UUID> userUuids = users.stream()
        .map(User::getUuid)
        .collect(Collectors.toList());
    return new UpdateTagResponseDto(tag.getUuid(), tag.getName(), postUuids, userUuids);
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
    if (tag == null) {
      return null;
    }
    List<UUID> postUuids = posts.stream()
        .map(Post::getUuid)
        .collect(Collectors.toList());
    List<UUID> userUuids = users.stream()
        .map(User::getUuid)
        .collect(Collectors.toList());
    return new FindTagByPatternResponseDto(tag.getUuid(), tag.getName(), postUuids, userUuids);
  }

  public FindTagByUuidResponseDto toFindByUuidResponseDto(Tag tag, List<Post> posts, List<User> users) {
    if (tag == null) {
      return null;
    }
    List<UUID> postUuids = posts.stream()
        .map(Post::getUuid)
        .collect(Collectors.toList());
    List<UUID> userUuids = users.stream()
        .map(User::getUuid)
        .collect(Collectors.toList());
    return new FindTagByUuidResponseDto(tag.getUuid(), tag.getName(), postUuids, userUuids);
  }
}