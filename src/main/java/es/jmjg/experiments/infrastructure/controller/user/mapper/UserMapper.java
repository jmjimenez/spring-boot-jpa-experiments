package es.jmjg.experiments.infrastructure.controller.user.mapper;

import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.application.user.dto.UpdateUserDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import es.jmjg.experiments.infrastructure.controller.shared.paged.PagedResponseDto;
import es.jmjg.experiments.infrastructure.controller.shared.paged.PagedResponseMapper;
import es.jmjg.experiments.infrastructure.controller.user.dto.PasswordResetResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserRequestDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.UpdateUserRequestDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
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

  public SaveUserDto toSaveUserDto(@Valid SaveUserRequestDto saveUserDto, AuthenticatedUserDto authenticatedUserDto) {
    return new SaveUserDto(
      saveUserDto.getId(),
      saveUserDto.getName(),
      saveUserDto.getEmail(),
      saveUserDto.getUsername(),
      saveUserDto.getPassword(),
      authenticatedUserDto
    );
  }

  public SaveUserResponseDto toSaveUserResponseDto(User savedUser) {
    return mapToResponseDto(savedUser, (uuid, name, email, username, posts, tags, comments) -> new SaveUserResponseDto(uuid, name,
        email, username));
  }

  public UpdateUserDto toUpdateUserDto(@Valid UpdateUserRequestDto updateUserDto, UserUuid id, AuthenticatedUserDto authenticatedUserDto) {
    return new UpdateUserDto(
      id,
      updateUserDto.getName(),
      updateUserDto.getEmail(),
      authenticatedUserDto);
  }

  public UpdateUserResponseDto toUpdateUserResponseDto(User updatedUser) {
    return mapToResponseDto(updatedUser, UpdateUserResponseDto::new);
  }

  public PasswordResetResponseDto toPasswordResetResponseDto(String resetKey) {
    return new PasswordResetResponseDto(resetKey);
  }

  public @NotNull AuthenticatedUserDto toAuthenticatedUserDto(JwtUserDetails userDetails) {
    return new AuthenticatedUserDto(userDetails.id, userDetails.getUsername(), userDetails.getPassword(),
      userDetails.getAuthorities());
  }

  public PagedResponseDto<FindAllUsersResponseDto> toPagedResponseDto(Page<User> users) {
    return PagedResponseMapper.createPagedResponse(users, this::toFindAllUsersResponseDto);
  }

  @FunctionalInterface
  private interface DtoConstructor<T> {
    T create(UserUuid uuid, String name, String email, String username, List<PostUuid> posts, List<String> tags, List<PostCommentUuid> comments);
  }

  private <T> T mapToResponseDto(User user, DtoConstructor<T> dtoConstructor) {
    if (user == null) {
      return null;
    }

    List<PostUuid> postUuids = user.getPosts() != null ? user.getPosts().stream()
        .map(Post::getPostUuid)
        .collect(Collectors.toList()) : List.of();

    List<String> tagNames = user.getTags() != null ? user.getTags().stream()
        .map(Tag::getName)
        .collect(Collectors.toList()) : List.of();

    List<PostCommentUuid> commentUuids = user.getComments() != null ? user.getComments().stream()
        .map(PostComment::getCommentUuid)
        .collect(Collectors.toList()) : List.of();

    return dtoConstructor.create(
        user.getUserUuid(), user.getName(), user.getEmail(), user.getUsername(),
        postUuids, tagNames, commentUuids);
  }

  private <T> List<T> mapToResponseDtoList(List<User> users, Function<User, T> mapper) {
    if (users == null) {
      return List.of();
    }
    return users.stream().map(mapper).collect(Collectors.toList());
  }
}
