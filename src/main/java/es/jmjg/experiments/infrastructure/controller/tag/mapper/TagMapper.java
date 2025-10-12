package es.jmjg.experiments.infrastructure.controller.tag.mapper;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.tag.dto.DeleteTagDto;
import es.jmjg.experiments.application.tag.dto.SaveTagDto;
import es.jmjg.experiments.application.tag.dto.UpdateTagDto;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.tag.entity.TagUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagRequestDto;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByPatternResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagRequestDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagResponseDto;

@Component
public class TagMapper {

  public SaveTagResponseDto toSaveTagResponseDto(Tag tag) {
    return mapToResponseDto(tag, List.of(), List.of(),
      SaveTagResponseDto::new);
  }

  public UpdateTagResponseDto toUpdateTagResponseDto(Tag updatedTag) {
    return mapToResponseDto(updatedTag, List.of(), List.of(),
      UpdateTagResponseDto::new);
  }

  public FindTagByPatternResponseDto toFindByPatternResponseDto(Tag tag, List<Post> posts, List<User> users) {
    return mapToResponseDto(tag, posts, users,
      FindTagByPatternResponseDto::new);
  }

  public FindTagByUuidResponseDto toFindByUuidResponseDto(Tag tag, List<Post> posts, List<User> users) {
    return mapToResponseDto(tag, posts, users,
      FindTagByUuidResponseDto::new);
  }

  public DeleteTagDto toDeleteTagDto(TagUuid uuid, AuthenticatedUserDto authenticatedUser) {
    return new DeleteTagDto(uuid, authenticatedUser);
  }

  public SaveTagDto toSaveTagDto(SaveTagRequestDto dto, AuthenticatedUserDto authenticatedUser) {
    return new SaveTagDto(
      dto.getUuid(),
      dto.getName(),
      authenticatedUser
    );
  }

  public UpdateTagDto toUpdateTagDto(TagUuid tagUuid, UpdateTagRequestDto dto, AuthenticatedUserDto userDto) {
    return new UpdateTagDto(
      tagUuid,
      dto.getName(),
      userDto
    );
  }

  @FunctionalInterface
  private interface TagDtoConstructor<T> {
    T create(TagUuid uuid, String name, List<PostUuid> posts, List<UserUuid> users);
  }

  private <T> T mapToResponseDto(Tag tag, List<Post> posts, List<User> users, TagDtoConstructor<T> dtoConstructor) {
    if (tag == null) {
      return null;
    }

    List<PostUuid> postUuids = posts.stream()
        .map(Post::getPostUuid)
        .collect(Collectors.toList());

    List<UserUuid> userUuids = users.stream()
        .map(User::getUserUuid)
        .collect(Collectors.toList());

    return dtoConstructor.create(tag.getTagUuid(), tag.getName(), postUuids, userUuids);
  }
}
