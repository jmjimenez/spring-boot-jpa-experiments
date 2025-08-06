package es.jmjg.experiments.infrastructure.controller.tag;

import java.util.List;
import java.util.UUID;

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

import es.jmjg.experiments.application.tag.DeleteTagByUuid;
import es.jmjg.experiments.application.tag.FindPostsByTag;
import es.jmjg.experiments.application.tag.FindTagByPattern;
import es.jmjg.experiments.application.tag.FindTagByUuid;
import es.jmjg.experiments.application.tag.FindUsersByTag;
import es.jmjg.experiments.application.tag.SaveTag;
import es.jmjg.experiments.application.tag.UpdateTagName;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindAllPostsResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByTagNameResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByTagResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.mapper.PostMapper;
import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByPatternResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagRequestDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagRequestDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.mapper.TagMapper;
import es.jmjg.experiments.infrastructure.controller.user.dto.UserResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Tag management operations")
public class TagController {

  private final TagMapper tagMapper;
  private final UserMapper userMapper;
  private final PostMapper postMapper;
  private final SaveTag saveTag;
  private final UpdateTagName updateTagName;
  private final DeleteTagByUuid deleteTagByUuid;
  private final FindTagByPattern findTagByPattern;
  private final FindUsersByTag findUsersByTag;
  private final FindPostsByTag findPostsByTag;
  private final FindTagByUuid findTagByUuid;

  public TagController(
      TagMapper tagMapper,
      UserMapper userMapper,
      PostMapper postMapper,
      SaveTag saveTag,
      UpdateTagName updateTagName,
      DeleteTagByUuid deleteTagByUuid,
      FindTagByPattern findTagByPattern,
      FindUsersByTag findUsersByTag,
      FindPostsByTag findPostsByTag,
      FindTagByUuid findTagByUuid) {
    this.tagMapper = tagMapper;
    this.userMapper = userMapper;
    this.postMapper = postMapper;
    this.saveTag = saveTag;
    this.updateTagName = updateTagName;
    this.deleteTagByUuid = deleteTagByUuid;
    this.findTagByPattern = findTagByPattern;
    this.findUsersByTag = findUsersByTag;
    this.findPostsByTag = findPostsByTag;
    this.findTagByUuid = findTagByUuid;
  }

  @GetMapping("/search")
  @Transactional(readOnly = true)
  @Operation(summary = "Find tags by pattern", description = "Finds tags by name pattern")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved tags", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindTagByPatternResponseDto.class)))
  })
  List<FindTagByPatternResponseDto> findByPattern(
      @Parameter(description = "Pattern to search for in tag names") @RequestParam String pattern) {
    List<Tag> tags = findTagByPattern.findByPattern(pattern);
    return tags.stream()
        .map(tag -> tagMapper.toFindByPatternResponseDto(tag, tag.getPosts(), tag.getUsers()))
        .toList();
  }

  @GetMapping("/{uuid}")
  @Transactional(readOnly = true)
  @Operation(summary = "Get tag by UUID", description = "Retrieves a specific tag by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved tag", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindTagByUuidResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Tag not found")
  })
  FindTagByUuidResponseDto findByUuid(
      @Parameter(description = "UUID of the tag to retrieve") @PathVariable UUID uuid) {
    Tag tag = findTagByUuid.findByUuid(uuid);
    return tagMapper.toFindByUuidResponseDto(tag, tag.getPosts(), tag.getUsers());
  }

  @GetMapping("/{uuid}/users")
  @Transactional(readOnly = true)
  @Operation(summary = "Find users by tag", description = "Finds all users associated with a specific tag")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Tag not found")
  })
  List<UserResponseDto> findUsersByTag(
      @Parameter(description = "UUID of the tag") @PathVariable UUID uuid) {
    List<User> users = findUsersByTag.findByTagUuid(uuid);
    return userMapper.toResponseDtoList(users);
  }

  @GetMapping("/{uuid}/posts")
  @Transactional(readOnly = true)
  @Operation(summary = "Find posts by tag", description = "Finds all posts associated with a specific tag")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindAllPostsResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Tag not found")
  })
  List<FindPostByTagResponseDto> findPostsByTag(
      @Parameter(description = "UUID of the tag") @PathVariable UUID uuid) {
    List<Post> posts = findPostsByTag.findByTagUuid(uuid);
    return postMapper.toFindPostsByTagResponseDto(posts);
  }

  @GetMapping("/search/users")
  @Transactional(readOnly = true)
  @Operation(summary = "Find users by tag name", description = "Finds all users associated with a tag by name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Tag not found")
  })
  List<UserResponseDto> findUsersByTagName(
      @Parameter(description = "Name of the tag") @RequestParam String name) {
    List<User> users = findUsersByTag.findByTagName(name);
    return userMapper.toResponseDtoList(users);
  }

  @GetMapping("/search/posts")
  @Transactional(readOnly = true)
  @Operation(summary = "Find posts by tag name", description = "Finds all posts associated with a tag by name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindAllPostsResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Tag not found")
  })
  List<FindPostByTagNameResponseDto> findPostsByTagName(
      @Parameter(description = "Name of the tag") @RequestParam String name) {
    List<Post> posts = findPostsByTag.findByTagName(name);
    return postMapper.toFindPostsByTagNameResponseDto(posts);
  }

  @PostMapping("")
  @Operation(summary = "Create a new tag", description = "Creates a new tag with the provided data")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Tag created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaveTagResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  ResponseEntity<SaveTagResponseDto> save(@RequestBody @Valid SaveTagRequestDto tagDto) {
    Tag tag = tagMapper.toDomain(tagDto);
    Tag savedTag = saveTag.save(tag);
    SaveTagResponseDto responseDto = tagMapper.toSaveTagResponseDto(savedTag);

    String locationUrl = UriComponentsBuilder.fromPath("/api/tags/{uuid}")
        .buildAndExpand(savedTag.getUuid())
        .toUriString();

    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Location", locationUrl)
        .body(responseDto);
  }

  @PutMapping("/{uuid}")
  @Operation(summary = "Update a tag name", description = "Updates an existing tag name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tag updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateTagResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Tag not found"),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  UpdateTagResponseDto updateName(@PathVariable UUID uuid, @RequestBody @Valid UpdateTagRequestDto tagDto) {
    Tag updatedTag = updateTagName.updateName(uuid, tagDto.getName());
    return tagMapper.toUpdateTagResponseDto(updatedTag);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{uuid}")
  @Operation(summary = "Delete a tag by UUID", description = "Deletes a tag by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Tag deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Tag not found")
  })
  void deleteByUuid(@PathVariable UUID uuid) {
    deleteTagByUuid.deleteByUuid(uuid);
  }
}