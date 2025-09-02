package es.jmjg.experiments.infrastructure.controller.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
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

import es.jmjg.experiments.application.user.DeleteUser;
import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.application.user.dto.DeleteUserDto;
import es.jmjg.experiments.application.user.dto.FindAllUsersDto;
import es.jmjg.experiments.application.user.dto.FindUserByEmailDto;
import es.jmjg.experiments.application.user.dto.FindUserByUsernameDto;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.application.user.dto.UpdateUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.controller.exception.UserNotFoundException;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindAllUsersResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByEmailResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByUsernameResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserRequestDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.UpdateUserRequestDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.UpdateUserResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.mapper.UserMapper;
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
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

  private final UserMapper userMapper;
  private final SaveUser saveUser;
  private final UpdateUser updateUser;
  private final FindUserByUuid findUserByUuid;
  private final FindUserByEmail findUserByEmail;
  private final FindUserByUsername findUserByUsername;
  private final FindAllUsers findAllUsers;
  private final DeleteUser deleteUser;

  public UserController(
      UserMapper userMapper,
      SaveUser saveUser,
      UpdateUser updateUser,
      FindUserByUuid findUserByUuid,
      FindUserByEmail findUserByEmail,
      FindUserByUsername findUserByUsername,
      FindAllUsers findAllUsers,
      DeleteUser deleteUser) {
    this.userMapper = userMapper;
    this.saveUser = saveUser;
    this.updateUser = updateUser;
    this.findUserByUuid = findUserByUuid;
    this.findUserByEmail = findUserByEmail;
    this.findUserByUsername = findUserByUsername;
    this.findAllUsers = findAllUsers;
    this.deleteUser = deleteUser;
  }

  @GetMapping("")
  @Transactional(readOnly = true)
  @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindAllUsersResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden")
  })
  Page<FindAllUsersResponseDto> findAll(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      Pageable pageable) {
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, userMapper.toAuthenticatedUserDto(userDetails));
    Page<User> users = findAllUsers.findAll(findAllUsersDto);
    return users.map(userMapper::toFindAllUsersResponseDto);
  }

  @GetMapping("/{uuid}")
  @Transactional(readOnly = true)
  @Operation(summary = "Get user by UUID", description = "Retrieves a specific user by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindUserByUuidResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  FindUserByUuidResponseDto findByUuid(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @Parameter(description = "UUID of the user to retrieve") @PathVariable UUID uuid) {
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(uuid, userDetails);
    User user = findUserByUuid.findByUuid(findUserByUuidDto).orElseThrow(UserNotFoundException::new);
    return userMapper.toFindUserByUuidResponseDto(user);
  }

  @GetMapping("/search/email")
  @Transactional(readOnly = true)
  @Operation(summary = "Find user by email", description = "Finds a user by their email address")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindUserByEmailResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  FindUserByEmailResponseDto findByEmail(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @Parameter(description = "Email address to search for") @RequestParam String email) {
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(email, userDetails);
    User user = findUserByEmail.findByEmail(findUserByEmailDto).orElseThrow(UserNotFoundException::new);
    return userMapper.toFindUserByEmailResponseDto(user);
  }

  @GetMapping("/search/username")
  @Transactional(readOnly = true)
  @Operation(summary = "Find user by username", description = "Finds a user by their username")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindUserByUsernameResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  FindUserByUsernameResponseDto findByUsername(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @Parameter(description = "Username to search for") @RequestParam String username) {
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(username, userDetails);
    User user = findUserByUsername.findByUsername(findUserByUsernameDto).orElseThrow(UserNotFoundException::new);
    return userMapper.toFindUserByUsernameResponseDto(user);
  }

  @PostMapping("")
  @Operation(summary = "Create a new user", description = "Creates a new user with the provided data")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaveUserResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  ResponseEntity<SaveUserResponseDto> save(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @Parameter(description = "User data to create") @RequestBody @Valid SaveUserRequestDto userDto) {
    SaveUserDto saveUserDto = new SaveUserDto(
        userDto.getUuid(),
        userDto.getName(),
        userDto.getEmail(),
        userDto.getUsername(),
        userDto.getPassword(),
        userDetails);
    User savedUser = saveUser.save(saveUserDto);
    SaveUserResponseDto responseDto = userMapper.toSaveUserResponseDto(savedUser);

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
      @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateUserResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
  UpdateUserResponseDto update(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @Parameter(description = "UUID of the user to update") @PathVariable UUID uuid,
      @Parameter(description = "Updated user data") @RequestBody @Valid UpdateUserRequestDto userDto) {

    UpdateUserDto updateUserDto = new UpdateUserDto(
        uuid,
        userDto.getName(),
        userDto.getEmail(),
        userDetails);

    User updatedUser = updateUser.update(updateUserDto);
    return userMapper.toUpdateUserResponseDto(updatedUser);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{uuid}")
  @Operation(summary = "Delete a user by UUID", description = "Deletes a user by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  void deleteByUuid(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @PathVariable UUID uuid) {
    DeleteUserDto deleteUserDto = new DeleteUserDto(uuid, userMapper.toAuthenticatedUserDto(userDetails));
    deleteUser.delete(deleteUserDto);
  }
}
