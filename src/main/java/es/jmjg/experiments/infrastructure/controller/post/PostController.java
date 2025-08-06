package es.jmjg.experiments.infrastructure.controller.post;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import es.jmjg.experiments.application.post.DeletePostById;
import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.application.post.FindPosts;
import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.UpdatePost;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.infrastructure.controller.exception.PostNotFoundException;
import es.jmjg.experiments.infrastructure.controller.post.dto.PagedResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.PostRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.PostResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.mapper.PostMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Post management operations")
public class PostController {

  private static final Logger log = LoggerFactory.getLogger(PostController.class);
  private final PostMapper postMapper;
  private final FindPosts findPosts;
  private final UpdatePost updatePost;
  private final SavePost savePost;
  private final FindPostByUuid findPostByUuid;
  private final FindAllPosts findAllPosts;
  private final DeletePostById deletePostById;

  public PostController(
      PostMapper postMapper,
      FindPosts findPosts,
      UpdatePost updatePost,
      SavePost savePost,
      FindPostByUuid findPostByUuid,
      FindAllPosts findAllPosts,
      DeletePostById deletePostById) {
    this.postMapper = postMapper;
    this.findPosts = findPosts;
    this.updatePost = updatePost;
    this.savePost = savePost;
    this.findPostByUuid = findPostByUuid;
    this.findAllPosts = findAllPosts;
    this.deletePostById = deletePostById;
  }

  @GetMapping("")
  @Transactional(readOnly = true)
  @Operation(summary = "Get all posts", description = "Retrieves a paginated list of all posts")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedResponseDto.class)))
  })
  PagedResponseDto<PostResponseDto> findAll(
      @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size);
    var postsPage = findAllPosts.findAll(pageable);
    return postMapper.toPagedResponseDto(postsPage);
  }

  @GetMapping("/{uuid}")
  @Operation(summary = "Get post by UUID", description = "Retrieves a specific post by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved post", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Post not found")
  })
  PostResponseDto findByUuid(
      @Parameter(description = "UUID of the post to retrieve") @PathVariable UUID uuid) {
    Post post = findPostByUuid.findByUuid(uuid).orElseThrow(PostNotFoundException::new);
    return postMapper.toResponseDto(post);
  }

  @GetMapping("/search")
  @Transactional(readOnly = true)
  @Operation(summary = "Search posts by content", description = "Finds posts containing specified words")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved matching posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponseDto.class)))
  })
  List<PostResponseDto> searchPosts(
      @Parameter(description = "Search terms to find in post content") @RequestParam String q,
      @Parameter(description = "Maximum number of results to return") @RequestParam(defaultValue = "20") int limit) {

    log.info("Searching posts with query: '{}' and limit: {}", q, limit);

    List<Post> posts = findPosts.find(q, limit);
    return postMapper.toResponseDtoList(posts);
  }

  @PostMapping("")
  @Operation(summary = "Create a new post", description = "Creates a new post with the provided data")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Post created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  ResponseEntity<PostResponseDto> save(@RequestBody @Valid PostRequestDto postDto) {
    Post post = postMapper.toDomain(postDto);
    Post savedPost = savePost.save(post, postDto.getUserId(), postDto.getTagNames());
    PostResponseDto responseDto = postMapper.toResponseDto(savedPost);

    String locationUrl = UriComponentsBuilder.fromPath("/api/posts/{uuid}")
        .buildAndExpand(savedPost.getUuid())
        .toUriString();

    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Location", locationUrl)
        .body(responseDto);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a post", description = "Updates an existing post with the provided data")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Post updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Post not found"),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  PostResponseDto update(@PathVariable Integer id, @RequestBody @Valid PostRequestDto postDto) {
    Post post = postMapper.toDomain(postDto);
    Post updatedPost = updatePost.update(id, post, postDto.getUserId(), postDto.getTagNames());
    return postMapper.toResponseDto(updatedPost);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a post", description = "Deletes a post by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Post not found")
  })
  void delete(@PathVariable Integer id) {
    deletePostById.deleteById(id);
  }
}
