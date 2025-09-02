package es.jmjg.experiments.infrastructure.controller.post;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import es.jmjg.experiments.application.post.DeletePost;
import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.application.post.FindPosts;
import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.UpdatePost;
import es.jmjg.experiments.application.post.dto.DeletePostDto;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.controller.exception.PostNotFoundException;
import es.jmjg.experiments.infrastructure.controller.user.mapper.UserMapper;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindAllPostsResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.PagedResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SearchPostsResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostResponseDto;
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

  public PostController(
      PostMapper postMapper,
      UserMapper userMapper,
      FindPosts findPosts,
      UpdatePost updatePost,
      SavePost savePost,
      FindPostByUuid findPostByUuid,
      FindAllPosts findAllPosts,
      DeletePost deletePost) {
    this.postMapper = postMapper;
    this.userMapper = userMapper;
    this.findPosts = findPosts;
    this.updatePost = updatePost;
    this.savePost = savePost;
    this.findPostByUuid = findPostByUuid;
    this.findAllPosts = findAllPosts;
    this.deletePost = deletePost;
  }

  @GetMapping("")
  @Transactional(readOnly = true)
  @Operation(summary = "Get all posts", description = "Retrieves a paginated list of all posts", security = {})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedResponseDto.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  PagedResponseDto<FindAllPostsResponseDto> findAll(
      @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size);
    var postsPage = findAllPosts.findAll(pageable);

    return postMapper.toPagedResponseDto(postsPage);
  }

  @GetMapping("/{uuid}")
  @Transactional(readOnly = true)
  @Operation(summary = "Get post by UUID", description = "Retrieves a specific post by its UUID", security = {})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved post", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindPostByUuidResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Post not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  FindPostByUuidResponseDto findByUuid(
      @Parameter(description = "UUID of the post to retrieve") @PathVariable UUID uuid) {

    Post post = findPostByUuid.findByUuid(uuid).orElseThrow(PostNotFoundException::new);
    return postMapper.toFindByUuidResponseDto(post);
  }

  @GetMapping("/search")
  @Transactional(readOnly = true)
  @Operation(summary = "Search posts by content", description = "Finds posts containing specified words", security = {})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved matching posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchPostsResponseDto.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error")
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
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<SavePostResponseDto> save(
      @Parameter(description = "Post data to create", required = true) @RequestBody @Valid SavePostRequestDto postDto,
      @AuthenticationPrincipal JwtUserDetails userDetails) {

    UUID userUuid = userDetails.id;
    var savePostDto = postMapper.toSavePostDto(postDto, userUuid);
    Post savedPost = savePost.save(savePostDto);
    SavePostResponseDto responseDto = postMapper.toSavePostResponseDto(savedPost);

    String locationUrl = UriComponentsBuilder.fromPath("/api/posts/{uuid}")
        .buildAndExpand(savedPost.getUuid())
        .toUriString();

    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Location", locationUrl)
        .body(responseDto);
  }

  @PutMapping("/{uuid}")
  @Transactional
  @Operation(summary = "Update a post", description = "Updates an existing post with the provided data")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Post updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdatePostResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
      @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to update this post"),
      @ApiResponse(responseCode = "404", description = "Post not found"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  UpdatePostResponseDto update(
      @Parameter(description = "UUID of the post to update") @PathVariable UUID uuid,
      @Parameter(description = "Updated post data", required = true) @RequestBody @Valid UpdatePostRequestDto postDto,
      @AuthenticationPrincipal JwtUserDetails userDetails) {

    var updatePostDto = postMapper.toUpdatePostDto(postDto, uuid, userDetails);
    Post updatedPost = updatePost.update(updatePostDto);
    return postMapper.toUpdatePostResponseDto(updatedPost);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{uuid}")
  @Transactional
  @Operation(summary = "Delete a post", description = "Deletes a post by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
      @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to delete this post"),
      @ApiResponse(responseCode = "404", description = "Post not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  void delete(@Parameter(description = "UUID of the post to delete") @PathVariable UUID uuid,
      @AuthenticationPrincipal JwtUserDetails userDetails) {

    var deletePostDto = new DeletePostDto(uuid, userMapper.toAuthenticatedUserDto(userDetails));
    deletePost.delete(deletePostDto);
  }
}
