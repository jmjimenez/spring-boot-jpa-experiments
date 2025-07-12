package es.jmjg.experiments.infrastructure.controller.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.infrastructure.controller.dto.PostRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.PostResponseDto;

@Component
public class PostMapper {

  public PostResponseDto toResponseDto(Post post) {
    if (post == null) {
      return null;
    }
    return new PostResponseDto(
        post.getId(), post.getUuid(), post.getUser().getId(), post.getTitle(), post.getBody());
  }

  public List<PostResponseDto> toResponseDtoList(List<Post> posts) {
    if (posts == null) {
      return List.of();
    }
    return posts.stream().map(this::toResponseDto).collect(Collectors.toList());
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
