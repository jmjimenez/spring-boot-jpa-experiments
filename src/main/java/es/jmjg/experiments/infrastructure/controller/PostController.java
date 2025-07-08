package es.jmjg.experiments.infrastructure.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
import es.jmjg.experiments.application.FindPosts;
import es.jmjg.experiments.application.PostService;
import es.jmjg.experiments.application.UpdatePost;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.infrastructure.controller.dto.PostRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.PostResponseDto;
import es.jmjg.experiments.infrastructure.controller.exception.PostNotFoundException;
import es.jmjg.experiments.infrastructure.controller.mapper.PostMapper;
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
        private final PostService postService;
        private final PostMapper postMapper;
        private final FindPosts findPosts;
        private final UpdatePost updatePost;

        public PostController(PostService postService, PostMapper postMapper, FindPosts findPosts,
                        UpdatePost updatePost) {
                this.postService = postService;
                this.postMapper = postMapper;
                this.findPosts = findPosts;
                this.updatePost = updatePost;
        }

        @GetMapping("")
        @Transactional(readOnly = true)
        @Operation(summary = "Get all posts", description = "Retrieves a list of all posts")
        @ApiResponses(value = {@ApiResponse(responseCode = "200",
                        description = "Successfully retrieved posts",
                        content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponseDto.class)))})
        List<PostResponseDto> findAll() {
                List<Post> posts = postService.findAll();
                return postMapper.toResponseDtoList(posts);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get post by ID", description = "Retrieves a specific post by its ID")
        @ApiResponses(value = {@ApiResponse(responseCode = "200",
                        description = "Successfully retrieved post",
                        content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Post not found")})
        PostResponseDto findById(@Parameter(
                        description = "ID of the post to retrieve") @PathVariable Integer id) {
                Post post = postService.findById(id).orElseThrow(PostNotFoundException::new);
                return postMapper.toResponseDto(post);
        }

        @GetMapping("/search")
        @Transactional(readOnly = true)
        @Operation(summary = "Search posts by content",
                        description = "Finds posts containing specified words")
        @ApiResponses(value = {@ApiResponse(responseCode = "200",
                        description = "Successfully retrieved matching posts",
                        content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponseDto.class)))})
        List<PostResponseDto> searchPosts(@Parameter(
                        description = "Search terms to find in post content") @RequestParam String q,
                        @Parameter(description = "Maximum number of results to return") @RequestParam(
                                        defaultValue = "20") int limit) {

                log.info("Searching posts with query: '{}' and limit: {}", q, limit);

                List<Post> posts = findPosts.find(q, limit);
                return postMapper.toResponseDtoList(posts);
        }

        @PostMapping("")
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(summary = "Create a new post",
                        description = "Creates a new post with the provided data")
        @ApiResponses(value = {@ApiResponse(responseCode = "201",
                        description = "Post created successfully",
                        content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")})
        PostResponseDto save(@RequestBody @Valid PostRequestDto postDto) {
                Post post = postMapper.toDomain(postDto);
                Post savedPost = postService.save(post, postDto.getUserId());
                return postMapper.toResponseDto(savedPost);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update a post",
                        description = "Updates an existing post with the provided data")
        @ApiResponses(value = {@ApiResponse(responseCode = "200",
                        description = "Post updated successfully",
                        content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Post not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")})
        PostResponseDto update(@PathVariable Integer id,
                        @RequestBody @Valid PostRequestDto postDto) {
                Post post = postMapper.toDomain(postDto);
                Post updatedPost = updatePost.update(id, post, postDto.getUserId());
                return postMapper.toResponseDto(updatedPost);
        }

        @ResponseStatus(HttpStatus.NO_CONTENT)
        @DeleteMapping("/{id}")
        @Operation(summary = "Delete a post", description = "Deletes a post by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204",
                                        description = "Post deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Post not found")})
        void delete(@PathVariable Integer id) {
                postService.deleteById(id);
        }
}
