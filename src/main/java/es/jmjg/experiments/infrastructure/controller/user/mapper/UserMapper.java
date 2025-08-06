package es.jmjg.experiments.infrastructure.controller.user.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindAllUsersResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByEmailResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByUsernameResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.UpdateUserResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserRequestDto;

@Component
public class UserMapper {

  public FindAllUsersResponseDto toFindAllUsersResponseDto(User user) {
    if (user == null) {
      return null;
    }

    List<UUID> postUuids = user.getPosts() != null ? user.getPosts().stream()
        .map(post -> post.getUuid())
        .collect(Collectors.toList()) : List.of();

    List<String> tagNames = user.getTags() != null ? user.getTags().stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList()) : List.of();

    return new FindAllUsersResponseDto(
        user.getUuid(), user.getName(), user.getEmail(), user.getUsername(),
        postUuids, tagNames);
  }

  public List<FindAllUsersResponseDto> toFindAllUsersResponseDtoList(List<User> users) {
    if (users == null) {
      return List.of();
    }
    return users.stream().map(this::toFindAllUsersResponseDto).collect(Collectors.toList());
  }

  public FindUserByUuidResponseDto toFindUserByUuidResponseDto(User user) {
    if (user == null) {
      return null;
    }

    List<UUID> postUuids = user.getPosts() != null ? user.getPosts().stream()
        .map(post -> post.getUuid())
        .collect(Collectors.toList()) : List.of();

    List<String> tagNames = user.getTags() != null ? user.getTags().stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList()) : List.of();

    return new FindUserByUuidResponseDto(
        user.getUuid(), user.getName(), user.getEmail(), user.getUsername(),
        postUuids, tagNames);
  }

  public List<FindUserByUuidResponseDto> toFindUserByUuidResponseDtoList(List<User> users) {
    if (users == null) {
      return List.of();
    }
    return users.stream().map(this::toFindUserByUuidResponseDto).collect(Collectors.toList());
  }

  public FindUserByEmailResponseDto toFindUserByEmailResponseDto(User user) {
    if (user == null) {
      return null;
    }

    List<UUID> postUuids = user.getPosts() != null ? user.getPosts().stream()
        .map(post -> post.getUuid())
        .collect(Collectors.toList()) : List.of();

    List<String> tagNames = user.getTags() != null ? user.getTags().stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList()) : List.of();

    return new FindUserByEmailResponseDto(
        user.getUuid(), user.getName(), user.getEmail(), user.getUsername(),
        postUuids, tagNames);
  }

  public FindUserByUsernameResponseDto toFindUserByUsernameResponseDto(User user) {
    if (user == null) {
      return null;
    }

    List<UUID> postUuids = user.getPosts() != null ? user.getPosts().stream()
        .map(post -> post.getUuid())
        .collect(Collectors.toList()) : List.of();

    List<String> tagNames = user.getTags() != null ? user.getTags().stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList()) : List.of();

    return new FindUserByUsernameResponseDto(
        user.getUuid(), user.getName(), user.getEmail(), user.getUsername(),
        postUuids, tagNames);
  }

  public List<FindUserByUsernameResponseDto> toFindUserByUsernameResponseDtoList(List<User> users) {
    if (users == null) {
      return List.of();
    }
    return users.stream().map(this::toFindUserByUsernameResponseDto).collect(Collectors.toList());
  }

  public SaveUserResponseDto toSaveUserResponseDto(User savedUser) {
    if (savedUser == null) {
      return null;
    }

    return new SaveUserResponseDto(
        savedUser.getUuid(), savedUser.getName(), savedUser.getEmail(), savedUser.getUsername(),
        savedUser.getPosts().stream().map(post -> post.getUuid()).collect(Collectors.toList()),
        savedUser.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toList()));
  }

  public UpdateUserResponseDto toUpdateUserResponseDto(User updatedUser) {
    if (updatedUser == null) {
      return null;
    }

    return new UpdateUserResponseDto(
        updatedUser.getUuid(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getUsername(),
        updatedUser.getPosts().stream().map(post -> post.getUuid()).collect(Collectors.toList()),
        updatedUser.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toList()));
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
    return user;
  }
}
