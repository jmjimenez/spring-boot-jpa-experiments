package es.jmjg.experiments.infrastructure.controller.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.infrastructure.controller.dto.PagedResponseDto;
import es.jmjg.experiments.infrastructure.controller.dto.PostRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.PostResponseDto;

@Component
public class PostMapper {

  public PostResponseDto toResponseDto(Post post) {
    if (post == null) {
      return null;
    }
    return new PostResponseDto(
        post.getId(), post.getUuid(), post.getUser().getUuid(), post.getTitle(), post.getBody());
  }

  public List<PostResponseDto> toResponseDtoList(List<Post> posts) {
    if (posts == null) {
      return List.of();
    }
    return posts.stream().map(this::toResponseDto).collect(Collectors.toList());
  }

  public PagedResponseDto<PostResponseDto> toPagedResponseDto(Page<Post> page) {
    if (page == null) {
      return new PagedResponseDto<>(List.of(), 0, 0, 0, 0, false, false);
    }

    List<PostResponseDto> content = page.getContent().stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());

    return new PagedResponseDto<>(
        content,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.hasNext(),
        page.hasPrevious());
  }

  public Post toDomain(PostRequestDto postRequestDto) {
    if (postRequestDto == null) {
      return null;
    }
    Post post = new Post();
    post.setId(postRequestDto.getId());
    post.setUuid(postRequestDto.getUuid());
    post.setTitle(postRequestDto.getTitle());
    post.setBody(postRequestDto.getBody());
    return post;
  }
}
