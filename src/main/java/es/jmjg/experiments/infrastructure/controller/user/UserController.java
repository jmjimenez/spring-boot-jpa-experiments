package es.jmjg.experiments.infrastructure.controller.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
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

import es.jmjg.experiments.application.user.DeleteUserByUuid;
import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.FindUserById;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.controller.exception.UserNotFoundException;
import es.jmjg.experiments.infrastructure.controller.user.dto.UserRequestDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.UserResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management operations")
public class UserController {

  private final UserMapper userMapper;
  private final SaveUser saveUser;
  private final UpdateUser updateUser;
  private final FindUserByUuid findUserByUuid;
  private final FindUserByEmail findUserByEmail;
  private final FindUserByUsername findUserByUsername;
  private final FindAllUsers findAllUsers;
  private final DeleteUserByUuid deleteUserByUuid;

  public UserController(
      UserMapper userMapper,
      SaveUser saveUser,
      UpdateUser updateUser,
      FindUserById findUserById,
      FindUserByUuid findUserByUuid,
      FindUserByEmail findUserByEmail,
      FindUserByUsername findUserByUsername,
      FindAllUsers findAllUsers,
      DeleteUserByUuid deleteUserByUuid) {
    this.userMapper = userMapper;
    this.saveUser = saveUser;
    this.updateUser = updateUser;
    this.findUserByUuid = findUserByUuid;
    this.findUserByEmail = findUserByEmail;
    this.findUserByUsername = findUserByUsername;
    this.findAllUsers = findAllUsers;
    this.deleteUserByUuid = deleteUserByUuid;
  }

  @GetMapping("")
  @Transactional(readOnly = true)
  @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)))
  })
  Page<UserResponseDto> findAll(Pageable pageable) {
    Page<User> users = findAllUsers.findAll(pageable);
    return users.map(userMapper::toResponseDto);
  }

  @GetMapping("/{uuid}")
  @Transactional(readOnly = true)
  @Operation(summary = "Get user by UUID", description = "Retrieves a specific user by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  UserResponseDto findByUuid(
      @Parameter(description = "UUID of the user to retrieve") @PathVariable UUID uuid) {
    User user = findUserByUuid.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
    return userMapper.toResponseDto(user);
  }

  @GetMapping("/search/email")
  @Transactional(readOnly = true)
  @Operation(summary = "Find user by email", description = "Finds a user by their email address")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  UserResponseDto findByEmail(
      @Parameter(description = "Email address to search for") @RequestParam String email) {
    User user = findUserByEmail.findByEmail(email).orElseThrow(UserNotFoundException::new);
    return userMapper.toResponseDto(user);
  }

  @GetMapping("/search/username")
  @Transactional(readOnly = true)
  @Operation(summary = "Find user by username", description = "Finds a user by their username")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  UserResponseDto findByUsername(
      @Parameter(description = "Username to search for") @RequestParam String username) {
    User user = findUserByUsername.findByUsername(username).orElseThrow(UserNotFoundException::new);
    return userMapper.toResponseDto(user);
  }

  @PostMapping("")
  @Operation(summary = "Create a new user", description = "Creates a new user with the provided data")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  ResponseEntity<UserResponseDto> save(@RequestBody @Valid UserRequestDto userDto) {
    User user = userMapper.toDomain(userDto);
    User savedUser = saveUser.save(user);
    UserResponseDto responseDto = userMapper.toResponseDto(savedUser);

    String locationUrl = UriComponentsBuilder.fromPath("/api/users/{uuid}")
        .buildAndExpand(savedUser.getUuid())
        .toUriString();

    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Location", locationUrl)
        .body(responseDto);
  }

  @PutMapping("/{uuid}")
  @Operation(summary = "Update a user", description = "Updates an existing user with the provided data")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  UserResponseDto update(@PathVariable UUID uuid, @RequestBody @Valid UserRequestDto userDto) {
    User user = userMapper.toDomain(userDto);
    User existing = findUserByUuid.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
    User updatedUser = updateUser.update(existing.getId(), user);
    return userMapper.toResponseDto(updatedUser);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{uuid}")
  @Operation(summary = "Delete a user by UUID", description = "Deletes a user by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  void deleteByUuid(@PathVariable UUID uuid) {
    deleteUserByUuid.deleteByUuid(uuid);
  }
}
