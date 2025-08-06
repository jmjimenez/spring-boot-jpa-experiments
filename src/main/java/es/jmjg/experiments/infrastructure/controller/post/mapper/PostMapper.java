package es.jmjg.experiments.infrastructure.controller.post.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindAllPostsResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByTagNameResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByTagResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.PagedResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.PostTagResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SearchPostsResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostResponseDto;

@Component
public class PostMapper {

  public UpdatePostResponseDto toUpdatePostResponseDto(Post post) {
    if (post == null) {
      return null;
    }
    return new UpdatePostResponseDto(post.getUuid(), post.getUser().getUuid(), post.getTitle(), post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  public SavePostResponseDto toSavePostResponseDto(Post post) {
    if (post == null) {
      return null;
    }
    return new SavePostResponseDto(post.getUuid(), post.getUser().getUuid(), post.getTitle(), post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  public FindPostByUuidResponseDto toFindByUuidResponseDto(Post post) {
    if (post == null) {
      return null;
    }
    return new FindPostByUuidResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  public SearchPostsResponseDto toSearchPostsResponseDto(Post post) {
    if (post == null) {
      return null;
    }
    return new SearchPostsResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  public FindAllPostsResponseDto toFindAllPostsResponseDto(Post post) {
    if (post == null) {
      return null;
    }
    return new FindAllPostsResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  public List<FindAllPostsResponseDto> toFindAllPostsResponseDto(List<Post> posts) {
    if (posts == null) {
      return List.of();
    }
    return posts.stream().map(this::toFindAllPostsResponseDto).collect(Collectors.toList());
  }

  public FindPostByTagResponseDto toFindPostsByTagResponseDto(Post post) {
    if (post == null) {
      return null;
    }
    return new FindPostByTagResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  public List<FindPostByTagResponseDto> toFindPostsByTagResponseDto(List<Post> posts) {
    if (posts == null) {
      return List.of();
    }
    return posts.stream().map(this::toFindPostsByTagResponseDto).collect(Collectors.toList());
  }

  public FindPostByTagNameResponseDto toFindPostsByTagNameResponseDto(Post post) {
    if (post == null) {
      return null;
    }
    return new FindPostByTagNameResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  public List<FindPostByTagNameResponseDto> toFindPostsByTagNameResponseDto(List<Post> posts) {
    if (posts == null) {
      return List.of();
    }
    return posts.stream().map(this::toFindPostsByTagNameResponseDto).collect(Collectors.toList());
  }

  public List<SearchPostsResponseDto> toSearchPostsResponseDto(List<Post> posts) {
    if (posts == null) {
      return List.of();
    }
    return posts.stream().map(this::toSearchPostsResponseDto).collect(Collectors.toList());
  }

  public PagedResponseDto<FindAllPostsResponseDto> toPagedResponseDto(Page<Post> page) {
    if (page == null) {
      return new PagedResponseDto<>(List.of(), 0, 0, 0, 0, false, false);
    }

    List<FindAllPostsResponseDto> content = page.getContent().stream()
        .map(this::toFindAllPostsResponseDto)
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

  public Post toDomain(UpdatePostRequestDto postRequestDto) {
    if (postRequestDto == null) {
      return null;
    }
    Post post = new Post();
    post.setUuid(postRequestDto.getUuid());
    post.setTitle(postRequestDto.getTitle());
    post.setBody(postRequestDto.getBody());
    // Note: tagUuids will be handled in the service layer
    return post;
  }

  public Post toDomain(SavePostRequestDto postRequestDto) {
    if (postRequestDto == null) {
      return null;
    }
    Post post = new Post();
    post.setUuid(postRequestDto.getUuid());
    post.setTitle(postRequestDto.getTitle());
    post.setBody(postRequestDto.getBody());
    // Note: tagUuids will be handled in the service layer
    return post;
  }

  private List<PostTagResponseDto> convertTagsToPostTagResponseDto(List<Tag> tags) {
    if (tags == null || tags.isEmpty()) {
      return List.of();
    }
    return tags.stream()
        .filter(tag -> tag != null && tag.getUuid() != null && tag.getName() != null)
        .map(tag -> new PostTagResponseDto(tag.getUuid(), tag.getName()))
        .collect(Collectors.toList());
  }
}
