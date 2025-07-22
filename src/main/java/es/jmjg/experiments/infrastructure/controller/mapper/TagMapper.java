package es.jmjg.experiments.infrastructure.controller.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.controller.dto.TagRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.TagResponseDto;

@Component
public class TagMapper {

  public TagResponseDto toResponseDto(Tag tag) {
    if (tag == null) {
      return null;
    }
    return new TagResponseDto(tag.getUuid(), tag.getName());
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