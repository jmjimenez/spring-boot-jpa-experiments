package es.jmjg.experiments.infrastructure.controller.post;

import es.jmjg.experiments.application.post.dto.DeletePostCommentDto;
import es.jmjg.experiments.application.post.dto.SavePostCommentDto;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.infrastructure.controller.shared.paged.PagedResponseDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import java.util.List;
import java.util.UUID;

import es.jmjg.experiments.application.post.*;
import es.jmjg.experiments.infrastructure.controller.post.dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import es.jmjg.experiments.application.post.dto.DeletePostDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.controller.user.mapper.UserMapper;
import es.jmjg.experiments.infrastructure.controller.post.mapper.PostMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Post management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class PostController {

  private final PostMapper postMapper;
  private final UserMapper userMapper;
  private final FindPosts findPosts;
  private final UpdatePost updatePost;
  private final SavePost savePost;
  private final FindPostByUuid findPostByUuid;
  private final FindAllPosts findAllPosts;
  private final DeletePost deletePost;
  private final UpdatePostTags updatePostTags;
  private final SavePostComment savePostComment;
  private final FindPostCommentByUuid findPostCommentByUuid;
  private final DeletePostComment deletePostComment;

  public PostController(
    PostMapper postMapper,
    UserMapper userMapper,
    FindPosts findPosts,
    UpdatePost updatePost,
    SavePost savePost,
    FindPostByUuid findPostByUuid,
    FindAllPosts findAllPosts,
    DeletePost deletePost,
    UpdatePostTags updatePostTags,
    SavePostComment savePostComment,
    FindPostCommentByUuid findPostCommentByUuid,
    DeletePostComment deletePostComment) {
    this.postMapper = postMapper;
    this.userMapper = userMapper;
    this.findPosts = findPosts;
    this.updatePost = updatePost;
    this.savePost = savePost;
    this.findPostByUuid = findPostByUuid;
    this.findAllPosts = findAllPosts;
    this.deletePost = deletePost;
    this.updatePostTags = updatePostTags;
    this.savePostComment = savePostComment;
    this.findPostCommentByUuid = findPostCommentByUuid;
    this.deletePostComment = deletePostComment;
  }

  @GetMapping("")
  @Transactional(readOnly = true)
  @Operation(summary = "Get all posts", description = "Retrieves a paginated list of all posts")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindAllPostsPagedResponseDto.class))),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  PagedResponseDto<FindAllPostsResponseDto> findAll(
    @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
    @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    var postsPage = findAllPosts.findAll(postMapper.toFindAllPostsDto(pageable));

    return postMapper.toPagedResponseDto(postsPage);
  }

  @GetMapping("/{post-id}")
  @Transactional(readOnly = true)
  @Operation(summary = "Get post by id", description = "Retrieves a specific post by its id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved post", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindPostByUuidResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  FindPostByUuidResponseDto findByUuid(
    @Parameter(name = "post-id", description = "id of the post to retrieve") @PathVariable(name = "post-id") UUID id) {

    Post post = findPostByUuid.findByUuid(PostUuid.fromUuid(id));
    return postMapper.toFindByUuidResponseDto(post);
  }

  @GetMapping("/search")
  @Transactional(readOnly = true)
  @Operation(summary = "Search posts by content", description = "Finds posts containing specified words")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching posts", content = @Content(mediaType = "application/json", array = @ArraySchema(schema =
    @Schema(implementation = SearchPostsResponseDto.class)))),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  List<SearchPostsResponseDto> searchPosts(
    @Parameter(description = "Search terms to find in post content") @RequestParam String q,
    @Parameter(description = "Maximum number of results to return") @RequestParam(defaultValue = "20") int limit) {

    List<Post> posts = findPosts.find(q, limit);
    return postMapper.toSearchPostsResponseDto(posts);
  }

  @PostMapping("")
  @Transactional
  @Operation(summary = "Create a new post", description = "Creates a new post with the provided data")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Post created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SavePostResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  ResponseEntity<SavePostResponseDto> save(
    @Parameter(description = "Post data to create", required = true) @RequestBody @Valid SavePostRequestDto postDto,
    @AuthenticationPrincipal JwtUserDetails userDetails) {

    var savePostDto = postMapper.toSavePostDto(postDto, userMapper.toAuthenticatedUserDto(userDetails));
    Post savedPost = savePost.save(savePostDto);
    SavePostResponseDto responseDto = postMapper.toSavePostResponseDto(savedPost);

    String locationUrl = UriComponentsBuilder.fromPath("/api/posts/{post-id}")
      .buildAndExpand(savedPost.getPostUuid())
      .toUriString();

    return ResponseEntity.status(HttpStatus.CREATED)
      .header("Location", locationUrl)
      .body(responseDto);
  }

  @PutMapping("/{id}")
  @Transactional
  @Operation(summary = "Update a post", description = "Updates an existing post with the provided data")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Post updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdatePostResponseDto.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to update this post", content = @Content),
    @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  UpdatePostResponseDto update(
    @Parameter(description = "UUID of the post to update") @PathVariable UUID id,
    @Parameter(description = "Updated post data", required = true) @RequestBody @Valid UpdatePostRequestDto postDto,
    @AuthenticationPrincipal JwtUserDetails userDetails) {

    var updatePostDto = postMapper.toUpdatePostDto(postDto, id, userMapper.toAuthenticatedUserDto(userDetails));
    Post updatedPost = updatePost.update(updatePostDto);
    return postMapper.toUpdatePostResponseDto(updatedPost);
  }

  @PatchMapping("/{post-id}/tags")
  @Transactional
  @Operation(summary = "Update tags of post", description = "Updates tags linked to existing post")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Post updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdatePostTagsResponseDto.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to update this post", content = @Content),
    @ApiResponse(responseCode = "404", description = "Post or tag not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  UpdatePostTagsResponseDto updateTags(
    @Parameter(name = "post-id", description = "id of the post to update") @PathVariable(name = "post-id") UUID id,
    @Parameter(description = "Updated tags list", required = true) @RequestBody @Valid UpdatePostTagsRequestDto postDto,
    @AuthenticationPrincipal JwtUserDetails userDetails) {

    var updatePostTagsDto = postMapper.toUpdatePostTagsDto(postDto, id, userMapper.toAuthenticatedUserDto(userDetails));
    Post updatedPost = updatePostTags.update(updatePostTagsDto);
    return postMapper.toUpdatePostTagsResponseDto(updatedPost);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{post-id}")
  @Transactional
  @Operation(summary = "Delete a post", description = "Deletes a post by its id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Post deleted successfully", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to delete this post", content = @Content),
    @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  void delete(@Parameter(name="post-id", description = "id of the post to delete") @PathVariable(name = "post-id") UUID postId,
    @AuthenticationPrincipal JwtUserDetails userDetails) {

    var deletePostDto = new DeletePostDto(PostUuid.fromUuid(postId), userMapper.toAuthenticatedUserDto(userDetails));
    deletePost.delete(deletePostDto);
  }

  @PostMapping("/{post-id}/comments")
  @Transactional
  @Operation(summary = "Adds a comment", description = "Adds a comment to a post")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Post comment added successfully"),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
    @ApiResponse(responseCode = "404", description = "Post or User not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  ResponseEntity<SavePostCommentResponseDto> addComment(@Parameter(name = "post-id", description = "id of the post") @PathVariable(name = "post-id") UUID id,
    @Parameter(description = "Post comment data to create", required = true) @RequestBody @Valid SavePostCommentRequestDto requestDto,
    @AuthenticationPrincipal JwtUserDetails userDetails) {

    SavePostCommentDto savePostCommentDto = postMapper.toAddCommentDto(requestDto, PostUuid.fromUuid(id), userMapper.toAuthenticatedUserDto(userDetails));
    PostComment result = savePostComment.save(savePostCommentDto);

    String locationUrl = UriComponentsBuilder.fromPath("/api/posts/{post-id}/comments/{comment-id}")
      .buildAndExpand(result.getPost().getPostUuid(), result.getCommentUuid())
      .toUriString();

    return ResponseEntity.status(HttpStatus.CREATED)
      .header("Location", locationUrl)
      .body(postMapper.toSavePostCommentResponseDto(result));
  }

  @GetMapping("/{post-id}/comments/{comment-id}")
  @Transactional(readOnly = true)
  @Operation(summary = "Get post comment by id", description = "Retrieves a specific post comment by its id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved post comment", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindPostCommentByUuidResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Post or Post Comment not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  FindPostCommentByUuidResponseDto findByUuid(
    @Parameter(name = "post-id", description = "id of the post to retrieve") @PathVariable(name = "post-id") UUID postUuid,
    @Parameter(name = "comment-id", description = "id of the post comment to retrieve") @PathVariable(name = "comment-id") UUID commentUuid) {

    PostComment postComment = findPostCommentByUuid.findByUuid(PostUuid.fromUuid(postUuid), PostCommentUuid.fromUuid(commentUuid));
    return postMapper.toFindPostCommentByUuidResponseDto(postComment);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{post-id}/comments/{comment-id}")
  @Transactional
  @Operation(summary = "Delete a post comment", description = "Deletes a post comment by its id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Post comment deleted successfully", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to delete this post", content = @Content),
    @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  void deleteComment(@Parameter(name="post-id", description = "id of the post") @PathVariable(name = "post-id") UUID postId,
    @Parameter(name="comment-id", description = "id of the post comment to delete") @PathVariable(name = "comment-id") UUID commentId,
    @AuthenticationPrincipal JwtUserDetails userDetails) {

    var deletePostCommentDto = new DeletePostCommentDto(PostCommentUuid.fromUuid(commentId), PostUuid.fromUuid(
      postId), userMapper.toAuthenticatedUserDto(userDetails));
    deletePostComment.delete(deletePostCommentDto);
  }
}
