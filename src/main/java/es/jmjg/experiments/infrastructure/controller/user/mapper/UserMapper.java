package es.jmjg.experiments.infrastructure.controller.user.mapper;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.tag.entity.Tag;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindAllUsersResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByEmailResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByUsernameResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.UpdateUserResponseDto;
import jakarta.validation.constraints.NotNull;

@Component
public class UserMapper {
  public FindAllUsersResponseDto toFindAllUsersResponseDto(User user) {
    return mapToResponseDto(user, FindAllUsersResponseDto::new);
  }

  public List<FindAllUsersResponseDto> toFindAllUsersResponseDto(List<User> users) {
    return mapToResponseDtoList(users, this::toFindAllUsersResponseDto);
  }

  public FindUserByUuidResponseDto toFindUserByUuidResponseDto(User user) {
    return mapToResponseDto(user, FindUserByUuidResponseDto::new);
  }

  public FindUserByEmailResponseDto toFindUserByEmailResponseDto(User user) {
    return mapToResponseDto(user, FindUserByEmailResponseDto::new);
  }

  public FindUserByUsernameResponseDto toFindUserByUsernameResponseDto(User user) {
    return mapToResponseDto(user, FindUserByUsernameResponseDto::new);
  }

  public SaveUserResponseDto toSaveUserResponseDto(User savedUser) {
    return mapToResponseDto(savedUser, (uuid, name, email, username, posts, tags) -> new SaveUserResponseDto(uuid, name,
        email, username));
  }

  public UpdateUserResponseDto toUpdateUserResponseDto(User updatedUser) {
    return mapToResponseDto(updatedUser, UpdateUserResponseDto::new);
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
        .map(Post::getUuid)
        .collect(Collectors.toList()) : List.of();

    List<String> tagNames = user.getTags() != null ? user.getTags().stream()
        .map(Tag::getName)
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

  public @NotNull AuthenticatedUserDto toAuthenticatedUserDto(JwtUserDetails userDetails) {
    return new AuthenticatedUserDto(userDetails.id, userDetails.getUsername(), userDetails.getPassword(),
        userDetails.getAuthorities());
  }
}
