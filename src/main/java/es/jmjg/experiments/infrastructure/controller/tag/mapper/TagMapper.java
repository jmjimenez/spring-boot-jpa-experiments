package es.jmjg.experiments.infrastructure.controller.tag.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.controller.tag.dto.TagRequestDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.TagResponseDto;

@Component
public class TagMapper {

  public TagResponseDto toResponseDto(Tag tag) {
    if (tag == null) {
      return null;
    }
    return toResponseDtoWithRelations(tag, List.of(), List.of());
  }

  public TagResponseDto toResponseDtoWithRelations(Tag tag, List<Post> posts, List<User> users) {
    if (tag == null) {
      return null;
    }
    List<UUID> postUuids = posts.stream()
        .map(Post::getUuid)
        .collect(Collectors.toList());
    List<UUID> userUuids = users.stream()
        .map(User::getUuid)
        .collect(Collectors.toList());
    return new TagResponseDto(tag.getUuid(), tag.getName(), postUuids, userUuids);
  }

  public List<TagResponseDto> toResponseDtoList(List<Tag> tags) {
    if (tags == null) {
      return List.of();
    }
    return tags.stream().map(this::toResponseDto).collect(Collectors.toList());
  }

  public Tag toDomain(TagRequestDto tagRequestDto) {
    if (tagRequestDto == null) {
      return null;
    }
    Tag tag = new Tag();
    tag.setUuid(tagRequestDto.getUuid());
    tag.setName(tagRequestDto.getName());
    return tag;
  }
}