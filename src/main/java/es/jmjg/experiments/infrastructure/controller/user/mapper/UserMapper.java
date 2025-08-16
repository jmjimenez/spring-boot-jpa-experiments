package es.jmjg.experiments.infrastructure.controller.user.mapper;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindAllUsersResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByEmailResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByUsernameResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserRequestDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.UpdateUserRequestDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.UpdateUserResponseDto;

@Component
public class UserMapper {

  private final PasswordEncoder passwordEncoder;

  public UserMapper(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public FindAllUsersResponseDto toFindAllUsersResponseDto(User user) {
    return mapToResponseDto(user, (uuid, name, email, username, posts, tags) -> new FindAllUsersResponseDto(uuid, name,
        email, username, posts, tags));
  }

  public List<FindAllUsersResponseDto> toFindAllUsersResponseDto(List<User> users) {
    return mapToResponseDtoList(users, this::toFindAllUsersResponseDto);
  }

  public FindUserByUuidResponseDto toFindUserByUuidResponseDto(User user) {
    return mapToResponseDto(user, (uuid, name, email, username, posts, tags) -> new FindUserByUuidResponseDto(uuid,
        name, email, username, posts, tags));
  }

  public List<FindUserByUuidResponseDto> toFindUserByUuidResponseDto(List<User> users) {
    return mapToResponseDtoList(users, this::toFindUserByUuidResponseDto);
  }

  public FindUserByEmailResponseDto toFindUserByEmailResponseDto(User user) {
    return mapToResponseDto(user, (uuid, name, email, username, posts, tags) -> new FindUserByEmailResponseDto(uuid,
        name, email, username, posts, tags));
  }

  public FindUserByUsernameResponseDto toFindUserByUsernameResponseDto(User user) {
    return mapToResponseDto(user, (uuid, name, email, username, posts, tags) -> new FindUserByUsernameResponseDto(uuid,
        name, email, username, posts, tags));
  }

  public List<FindUserByUsernameResponseDto> toFindUserByUsernameResponseDto(List<User> users) {
    return mapToResponseDtoList(users, this::toFindUserByUsernameResponseDto);
  }

  public SaveUserResponseDto toSaveUserResponseDto(User savedUser) {
    return mapToResponseDto(savedUser, (uuid, name, email, username, posts, tags) -> new SaveUserResponseDto(uuid, name,
        email, username, posts, tags));
  }

  public UpdateUserResponseDto toUpdateUserResponseDto(User updatedUser) {
    return mapToResponseDto(updatedUser, (uuid, name, email, username, posts, tags) -> new UpdateUserResponseDto(uuid,
        name, email, username, posts, tags));
  }

  public User toDomain(SaveUserRequestDto userRequestDto) {
    if (userRequestDto == null) {
      return null;
    }
    User user = new User();
    user.setUuid(userRequestDto.getUuid());
    user.setName(userRequestDto.getName());
    user.setEmail(userRequestDto.getEmail());
    user.setUsername(userRequestDto.getUsername());
    user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
    return user;
  }

  public User toDomain(UpdateUserRequestDto userDto) {
    if (userDto == null) {
      return null;
    }
    User user = new User();
    user.setUuid(userDto.getUuid());
    user.setName(userDto.getName());
    user.setEmail(userDto.getEmail());
    user.setUsername(userDto.getUsername());
    return user;
  }

  @FunctionalInterface
  private interface DtoConstructor<T> {
    T create(UUID uuid, String name, String email, String username, List<UUID> posts, List<String> tags);
  }

  private <T> T mapToResponseDto(User user, DtoConstructor<T> dtoConstructor) {
    if (user == null) {
      return null;
    }

    List<UUID> postUuids = user.getPosts() != null ? user.getPosts().stream()
        .map(post -> post.getUuid())
        .collect(Collectors.toList()) : List.of();

    List<String> tagNames = user.getTags() != null ? user.getTags().stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList()) : List.of();

    return dtoConstructor.create(
        user.getUuid(), user.getName(), user.getEmail(), user.getUsername(),
        postUuids, tagNames);
  }

  private <T> List<T> mapToResponseDtoList(List<User> users, Function<User, T> mapper) {
    if (users == null) {
      return List.of();
    }
    return users.stream().map(mapper).collect(Collectors.toList());
  }
}
