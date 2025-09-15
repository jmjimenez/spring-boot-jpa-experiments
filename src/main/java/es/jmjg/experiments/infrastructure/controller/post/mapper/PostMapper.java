package es.jmjg.experiments.infrastructure.controller.post.mapper;

import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostCommentByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostCommentResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.TagDto;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
public class PostMapper {

  private static final Logger logger = LoggerFactory.getLogger(PostMapper.class);

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

  public SavePostDto toSavePostDto(SavePostRequestDto postRequestDto, AuthenticatedUserDto authenticatedUser) {
    return new SavePostDto(
        postRequestDto.getUuid(),
        postRequestDto.getTitle(),
        postRequestDto.getBody(),
        authenticatedUser,
        Optional.ofNullable(postRequestDto.getTagNames()).orElse(List.of()));
  }

  public UpdatePostDto toUpdatePostDto(UpdatePostRequestDto postRequestDto, UUID PostUuid, AuthenticatedUserDto authenticatedUser) {
    return new UpdatePostDto(
        PostUuid,
        postRequestDto.getTitle(),
        postRequestDto.getBody(),
        Optional.ofNullable(postRequestDto.getTagNames()).orElse(List.of()),
        authenticatedUser);
  }

  public UpdatePostTagsDto toUpdatePostTagsDto(UpdatePostTagsRequestDto postDto, UUID postUuid, AuthenticatedUserDto user) {
      return new UpdatePostTagsDto(postUuid, postDto.getTagNames(), user);
  }

  public UpdatePostTagsResponseDto toUpdatePostTagsResponseDto(Post post) {
        List<TagDto> tags = post.getTags() != null
            ? post.getTags().stream()
                .map(tag -> new TagDto(tag.getUuid(), tag.getName()))
                .collect(java.util.stream.Collectors.toList())
            : List.of();

    return new UpdatePostTagsResponseDto(post.getUuid(), post.getTitle(), tags);
  }

  public SavePostCommentResponseDto toSavePostCommentResponseDto(PostComment postComment) {
    return new SavePostCommentResponseDto(
      postComment.getUuid(),
      postComment.getUser().getUuid(),
      postComment.getPost().getUuid(),
      postComment.getComment()
    );
  }

  // Private helper methods for creating specific response DTOs
  private UpdatePostResponseDto createUpdatePostResponseDto(Post post) {
    logger.debug("Creating UpdatePostResponseDto for post UUID: {}", post.getUuid());
    try {
      UUID userUuid = post.getUser().getUuid();

      List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());

      return new UpdatePostResponseDto(
          post.getUuid(),
          userUuid,
          post.getTitle(),
          post.getBody(),
          tags);
    } catch (org.hibernate.LazyInitializationException e) {
      logger.error("LazyInitializationException in createUpdatePostResponseDto for post UUID: {}. Error: {}",
          post.getUuid(), e.getMessage(), e);
      throw e;
    }
  }

  private SavePostResponseDto createSavePostResponseDto(Post post) {
    logger.debug("Creating SavePostResponseDto for post UUID: {}", post.getUuid());
    try {
      UUID userUuid = post.getUser().getUuid();

      List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());

      return new SavePostResponseDto(
          post.getUuid(),
          userUuid,
          post.getTitle(),
          post.getBody(),
          tags);
    } catch (org.hibernate.LazyInitializationException e) {
      logger.error("LazyInitializationException in createSavePostResponseDto for post UUID: {}. Error: {}",
          post.getUuid(), e.getMessage(), e);
      throw e;
    }
  }

  private FindPostByUuidResponseDto createFindPostByUuidResponseDto(Post post) {
    try {
      UUID userUuid = post.getUser().getUuid();

      List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());

      return new FindPostByUuidResponseDto(
          post.getUuid(),
          userUuid,
          post.getTitle(),
          post.getBody(),
          tags);
    } catch (org.hibernate.LazyInitializationException e) {
      logger.error("LazyInitializationException in createFindPostByUuidResponseDto for post UUID: {}. Error: {}",
          post.getUuid(), e.getMessage(), e);
      throw e;
    }
  }

  private FindAllPostsResponseDto createFindAllPostsResponseDto(Post post) {
    logger.debug("Creating FindAllPostsResponseDto for post UUID: {}", post.getUuid());
    try {
      UUID userUuid = post.getUser().getUuid();

      List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());

      return new FindAllPostsResponseDto(
          post.getUuid(),
          userUuid,
          post.getTitle(),
          post.getBody(),
          tags);
    } catch (org.hibernate.LazyInitializationException e) {
      logger.error("LazyInitializationException in createFindAllPostsResponseDto for post UUID: {}. Error: {}",
          post.getUuid(), e.getMessage(), e);
      throw e;
    }
  }

  private FindPostByTagResponseDto createFindPostByTagResponseDto(Post post) {
    logger.debug("Creating FindPostByTagResponseDto for post UUID: {}", post.getUuid());
    try {
      UUID userUuid = post.getUser().getUuid();

      List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());

      return new FindPostByTagResponseDto(
          post.getUuid(),
          userUuid,
          post.getTitle(),
          post.getBody(),
          tags);
    } catch (org.hibernate.LazyInitializationException e) {
      logger.error("LazyInitializationException in createFindPostByTagResponseDto for post UUID: {}. Error: {}",
          post.getUuid(), e.getMessage(), e);
      throw e;
    }
  }

  private FindPostByTagNameResponseDto createFindPostByTagNameResponseDto(Post post) {
    logger.debug("Creating FindPostByTagNameResponseDto for post UUID: {}", post.getUuid());
    try {
      UUID userUuid = post.getUser().getUuid();

      List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());

      return new FindPostByTagNameResponseDto(
          post.getUuid(),
          userUuid,
          post.getTitle(),
          post.getBody(),
          tags);
    } catch (org.hibernate.LazyInitializationException e) {
      logger.error("LazyInitializationException in createFindPostByTagNameResponseDto for post UUID: {}. Error: {}",
          post.getUuid(), e.getMessage(), e);
      throw e;
    }
  }

  private SearchPostsResponseDto createSearchPostsResponseDto(Post post) {
    logger.debug("Creating SearchPostsResponseDto for post UUID: {}", post.getUuid());
    try {
      logger.debug("Accessing post.getUser().getUuid() - potential LazyInitializationException point");
      UUID userUuid = post.getUser().getUuid();
      logger.debug("Successfully accessed user UUID: {}", userUuid);

      logger.debug("Accessing post.getTags() - potential LazyInitializationException point");
      List<PostTagResponseDto> tags = convertTagsToPostTagResponseDto(post.getTags());
      logger.debug("Successfully accessed tags, count: {}", tags.size());

      return new SearchPostsResponseDto(
          post.getUuid(),
          userUuid,
          post.getTitle(),
          post.getBody(),
          tags);
    } catch (org.hibernate.LazyInitializationException e) {
      logger.error("LazyInitializationException in createSearchPostsResponseDto for post UUID: {}. Error: {}",
          post.getUuid(), e.getMessage(), e);
      throw e;
    }
  }

  private List<PostTagResponseDto> convertTagsToPostTagResponseDto(List<Tag> tags) {
    logger.debug("Converting tags to PostTagResponseDto, tags count: {}", tags != null ? tags.size() : 0);
    if (tags == null || tags.isEmpty()) {
      return List.of();
    }
    try {
      return tags.stream()
          .filter(tag -> tag != null && tag.getUuid() != null && tag.getName() != null)
          .map(tag -> {
            logger.debug("Processing tag UUID: {}, Name: {}", tag.getUuid(), tag.getName());
            return new PostTagResponseDto(tag.getUuid(), tag.getName());
          })
          .collect(Collectors.toList());
    } catch (org.hibernate.LazyInitializationException e) {
      logger.error("LazyInitializationException in convertTagsToPostTagResponseDto. Error: {}", e.getMessage(), e);
      throw e;
    }
  }

  public FindPostCommentByUuidResponseDto toFindPostCommentByUuidResponseDto(
    PostComment postComment) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    return new FindPostCommentByUuidResponseDto(
      postComment.getUuid(),
      postComment.getPost().getUuid(),
      postComment.getUser().getUuid(),
      postComment.getComment(),
      postComment.getCreatedAt() != null ? postComment.getCreatedAt().format(formatter) : null
    );
  }
}
