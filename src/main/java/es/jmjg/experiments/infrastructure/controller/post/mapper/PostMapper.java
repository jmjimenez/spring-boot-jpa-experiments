package es.jmjg.experiments.infrastructure.controller.post.mapper;

import java.util.List;
import java.util.function.Function;
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

  // Generic method for creating response DTOs from Post
  private <T> T createResponseDto(Post post, Function<Post, T> dtoCreator) {
    if (post == null) {
      return null;
    }
    return dtoCreator.apply(post);
  }

  // Generic method for converting lists
  private <T> List<T> convertList(List<Post> posts, Function<Post, T> converter) {
    if (posts == null) {
      return List.of();
    }
    return posts.stream()
        .map(converter)
        .collect(Collectors.toList());
  }

  // Generic method for creating paged responses
  private <T> PagedResponseDto<T> createPagedResponse(Page<Post> page, Function<Post, T> converter) {
    if (page == null) {
      return new PagedResponseDto<>(List.of(), 0, 0, 0, 0, false, false);
    }

    List<T> content = page.getContent().stream()
        .map(converter)
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

  // Generic method for domain conversion
  private Post createPostFromDto(Object dto, Function<Object, Post> converter) {
    if (dto == null) {
      return null;
    }
    return converter.apply(dto);
  }

  // Response DTO creation methods using the generic approach
  public UpdatePostResponseDto toUpdatePostResponseDto(Post post) {
    return createResponseDto(post, this::createUpdatePostResponseDto);
  }

  public SavePostResponseDto toSavePostResponseDto(Post post) {
    return createResponseDto(post, this::createSavePostResponseDto);
  }

  public FindPostByUuidResponseDto toFindByUuidResponseDto(Post post) {
    return createResponseDto(post, this::createFindPostByUuidResponseDto);
  }

  public FindAllPostsResponseDto toFindAllPostsResponseDto(Post post) {
    return createResponseDto(post, this::createFindAllPostsResponseDto);
  }

  public FindPostByTagResponseDto toFindPostsByTagResponseDto(Post post) {
    return createResponseDto(post, this::createFindPostByTagResponseDto);
  }

  public FindPostByTagNameResponseDto toFindPostsByTagNameResponseDto(Post post) {
    return createResponseDto(post, this::createFindPostByTagNameResponseDto);
  }

  public SearchPostsResponseDto toSearchPostsResponseDto(Post post) {
    return createResponseDto(post, this::createSearchPostsResponseDto);
  }

  // List conversion methods using the generic approach
  public List<FindAllPostsResponseDto> toFindAllPostsResponseDto(List<Post> posts) {
    return convertList(posts, this::toFindAllPostsResponseDto);
  }

  public List<FindPostByTagResponseDto> toFindPostsByTagResponseDto(List<Post> posts) {
    return convertList(posts, this::toFindPostsByTagResponseDto);
  }

  public List<FindPostByTagNameResponseDto> toFindPostsByTagNameResponseDto(List<Post> posts) {
    return convertList(posts, this::toFindPostsByTagNameResponseDto);
  }

  public List<SearchPostsResponseDto> toSearchPostsResponseDto(List<Post> posts) {
    return convertList(posts, this::toSearchPostsResponseDto);
  }

  // Paged response method using the generic approach
  public PagedResponseDto<FindAllPostsResponseDto> toPagedResponseDto(Page<Post> page) {
    return createPagedResponse(page, this::toFindAllPostsResponseDto);
  }

  // Domain conversion methods using the generic approach
  public Post toDomain(UpdatePostRequestDto postRequestDto) {
    return createPostFromDto(postRequestDto, this::createPostFromUpdateDto);
  }

  public Post toDomain(SavePostRequestDto postRequestDto) {
    return createPostFromDto(postRequestDto, this::createPostFromSaveDto);
  }

  // Private helper methods for creating specific response DTOs
  private UpdatePostResponseDto createUpdatePostResponseDto(Post post) {
    return new UpdatePostResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  private SavePostResponseDto createSavePostResponseDto(Post post) {
    return new SavePostResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  private FindPostByUuidResponseDto createFindPostByUuidResponseDto(Post post) {
    return new FindPostByUuidResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  private FindAllPostsResponseDto createFindAllPostsResponseDto(Post post) {
    return new FindAllPostsResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  private FindPostByTagResponseDto createFindPostByTagResponseDto(Post post) {
    return new FindPostByTagResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  private FindPostByTagNameResponseDto createFindPostByTagNameResponseDto(Post post) {
    return new FindPostByTagNameResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  private SearchPostsResponseDto createSearchPostsResponseDto(Post post) {
    return new SearchPostsResponseDto(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        convertTagsToPostTagResponseDto(post.getTags()));
  }

  // Private helper methods for domain conversion
  private Post createPostFromUpdateDto(Object dto) {
    UpdatePostRequestDto postRequestDto = (UpdatePostRequestDto) dto;
    Post post = new Post();
    post.setTitle(postRequestDto.getTitle());
    post.setBody(postRequestDto.getBody());
    // Note: tagUuids will be handled in the service layer
    return post;
  }

  private Post createPostFromSaveDto(Object dto) {
    SavePostRequestDto postRequestDto = (SavePostRequestDto) dto;
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
