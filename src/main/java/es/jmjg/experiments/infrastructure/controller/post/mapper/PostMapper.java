package es.jmjg.experiments.infrastructure.controller.post.mapper;

import es.jmjg.experiments.application.post.dto.FindAllPostsDto;
import es.jmjg.experiments.application.post.dto.SavePostCommentDto;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostCommentByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.PostCommentResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostCommentRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostCommentResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.TagDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import es.jmjg.experiments.application.post.dto.SavePostDto;
import es.jmjg.experiments.application.post.dto.UpdatePostDto;
import es.jmjg.experiments.application.post.dto.UpdatePostTagsDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
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
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostTagsRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostTagsResponseDto;

@Component
@Slf4j
public class PostMapper {
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

  public List<FindPostByTagResponseDto> toFindPostsByTagResponseDto(List<Post> posts) {
    return convertList(posts, this::toFindPostsByTagResponseDto);
  }

  public List<FindPostByTagNameResponseDto> toFindPostsByTagNameResponseDto(List<Post> posts) {
    return convertList(posts, this::toFindPostsByTagNameResponseDto);
  }

  public List<SearchPostsResponseDto> toSearchPostsResponseDto(List<Post> posts) {
    return convertList(posts, this::toSearchPostsResponseDto);
  }

  public PagedResponseDto<FindAllPostsResponseDto> toPagedResponseDto(Page<Post> page) {
    return createPagedResponse(page, this::toFindAllPostsResponseDto);
  }

  public SavePostDto toSavePostDto(SavePostRequestDto postRequestDto, AuthenticatedUserDto authenticatedUser) {
    return new SavePostDto(
      postRequestDto.getId(),
      postRequestDto.getTitle(),
      postRequestDto.getBody(),
      authenticatedUser,
      Optional.ofNullable(postRequestDto.getTagNames()).orElse(List.of()));
  }

  public UpdatePostDto toUpdatePostDto(UpdatePostRequestDto postRequestDto, UUID postUuid, AuthenticatedUserDto authenticatedUser) {
    return new UpdatePostDto(
      PostUuid.fromUuid(postUuid),
      postRequestDto.getTitle(),
      postRequestDto.getBody(),
      Optional.ofNullable(postRequestDto.getTagNames()).orElse(List.of()),
      authenticatedUser);
  }

  public UpdatePostTagsDto toUpdatePostTagsDto(UpdatePostTagsRequestDto postDto, UUID postUuid, AuthenticatedUserDto user) {
    return new UpdatePostTagsDto(PostUuid.fromUuid(postUuid), postDto.getTagNames(), user);
  }

  public UpdatePostTagsResponseDto toUpdatePostTagsResponseDto(Post post) {
    List<TagDto> tags = post.getTags() != null
      ? post.getTags().stream()
      .map(tag -> new TagDto(tag.getTagUuid(), tag.getName()))
      .collect(java.util.stream.Collectors.toList())
      : List.of();

    return new UpdatePostTagsResponseDto(post.getPostUuid(), post.getTitle(), tags);
  }

  public SavePostCommentResponseDto toSavePostCommentResponseDto(PostComment postComment) {
    return new SavePostCommentResponseDto(
      postComment.getUuid(),
      postComment.getUser().getUserUuid(),
      postComment.getPost().getPostUuid(),
      postComment.getComment(),
      postComment.getCreatedAt()
    );
  }

  public FindPostCommentByUuidResponseDto toFindPostCommentByUuidResponseDto(
    PostComment postComment) {
    return new FindPostCommentByUuidResponseDto(
      postComment.getUuid(),
      postComment.getUser().getUserUuid(),
      postComment.getPost().getPostUuid(),
      postComment.getComment(),
      postComment.getCreatedAt()
    );
  }

  public FindAllPostsDto toFindAllPostsDto(Pageable pageable) {
    return new FindAllPostsDto(pageable);
  }

  public SavePostCommentDto toAddCommentDto(@Valid SavePostCommentRequestDto requestDto, PostUuid id,
    @NotNull AuthenticatedUserDto authenticatedUserDto) {
    return new SavePostCommentDto(id, requestDto.getId(), requestDto.getComment(), authenticatedUserDto);
  }

  private UpdatePostResponseDto createUpdatePostResponseDto(Post post) {
    return createResponseDto(post, this::buildUpdatePostResponseDto);
  }

  private SavePostResponseDto createSavePostResponseDto(Post post) {
    List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());
    return new SavePostResponseDto(
      post.getPostUuid(),
      post.getUser().getUserUuid(),
      post.getTitle(),
      post.getBody(),
      tags);
  }

  private FindPostByUuidResponseDto createFindPostByUuidResponseDto(Post post) {
    return createResponseDto(post, this::buildFindPostByUuidResponseDto);
  }

  private FindAllPostsResponseDto createFindAllPostsResponseDto(Post post) {
    return createResponseDto(post, this::buildFindAllPostsResponseDto);
  }

  private FindPostByTagResponseDto createFindPostByTagResponseDto(Post post) {
    return createResponseDto(post, this::buildFindPostByTagResponseDto);
  }

  private FindPostByTagNameResponseDto createFindPostByTagNameResponseDto(Post post) {
    return createResponseDto(post, this::buildFindPostByTagNameResponseDto);
  }

  private SearchPostsResponseDto createSearchPostsResponseDto(Post post) {
    return createResponseDto(post, this::buildSearchPostsResponseDto);
  }

  private FindPostByUuidResponseDto buildFindPostByUuidResponseDto(Post post) {
    List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());
    List<PostCommentResponseDto> comments = convertCommentsToPostCommentResponseDto(post.getComments());
    return new FindPostByUuidResponseDto(
      post.getPostUuid(),
      post.getUser().getUserUuid(),
      post.getTitle(),
      post.getBody(),
      tags,
      comments);
  }

  private FindAllPostsResponseDto buildFindAllPostsResponseDto(Post post) {
    List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());
    List<PostCommentResponseDto> comments = convertCommentsToPostCommentResponseDto(post.getComments());
    return new FindAllPostsResponseDto(
      post.getPostUuid(),
      post.getUser().getUserUuid(),
      post.getTitle(),
      post.getBody(),
      tags,
      comments);
  }

  private FindPostByTagResponseDto buildFindPostByTagResponseDto(Post post) {
    List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());
    List<PostCommentResponseDto> comments = convertCommentsToPostCommentResponseDto(post.getComments());
    return new FindPostByTagResponseDto(
      post.getPostUuid(),
      post.getUser().getUserUuid(),
      post.getTitle(),
      post.getBody(),
      tags,
      comments);
  }

  private SearchPostsResponseDto buildSearchPostsResponseDto(Post post) {
    List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());
    return new SearchPostsResponseDto(
      post.getPostUuid(),
      post.getUser().getUserUuid(),
      post.getTitle(),
      post.getBody(),
      tags);
  }

  private FindPostByTagNameResponseDto buildFindPostByTagNameResponseDto(Post post) {
    List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());
    List<PostCommentResponseDto> comments = convertCommentsToPostCommentResponseDto(post.getComments());
    return new FindPostByTagNameResponseDto(
      post.getPostUuid(),
      post.getUser().getUserUuid(),
      post.getTitle(),
      post.getBody(),
      tags,
      comments);
  }

  private UpdatePostResponseDto buildUpdatePostResponseDto(Post post) {
    List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());
    return new UpdatePostResponseDto(
      post.getPostUuid(),
      post.getUser().getUserUuid(),
      post.getTitle(),
      post.getBody(),
      tags);
  }

  private <T> T createResponseDto(Post post, Function<Post, T> dtoCreator) {
    if (post == null) {
      return null;
    }
    return dtoCreator.apply(post);
  }

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

  private List<PostTagResponseDto> convertTagsToPostTagResponseDto(List<Tag> tags) {
    if (tags == null || tags.isEmpty()) {
      return List.of();
    }
    return tags.stream()
      .filter(tag -> tag != null && tag.getTagUuid() != null && tag.getName() != null)
      .map(tag -> new PostTagResponseDto(tag.getTagUuid(), tag.getName()))
      .collect(Collectors.toList());
  }

  private List<PostCommentResponseDto> convertCommentsToPostCommentResponseDto(List<PostComment> comments) {
    if (comments == null || comments.isEmpty()) {
      return List.of();
    }
    return comments.stream()
      .filter(comment -> comment != null && comment.getUuid() != null)
      .map(comment -> new PostCommentResponseDto(comment.getUuid()))
      .collect(Collectors.toList());
  }

  private <T> List<T> convertList(List<Post> posts, Function<Post, T> converter) {
    if (posts == null) {
      return List.of();
    }
    return posts.stream()
      .map(converter)
      .collect(Collectors.toList());
  }
}
