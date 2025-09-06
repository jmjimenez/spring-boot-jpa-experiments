package es.jmjg.experiments.shared;

import es.jmjg.experiments.application.tag.dto.DeleteTagDto;
import es.jmjg.experiments.application.tag.dto.SaveTagDto;
import es.jmjg.experiments.application.tag.dto.UpdateTagDto;
import es.jmjg.experiments.domain.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

import es.jmjg.experiments.domain.entity.Tag;

public class TagFactory {

  public static Tag createBasicTag() {
    Tag tag = new Tag();
    tag.setUuid(UUID.randomUUID());
    tag.setName("test-tag");
    return tag;
  }

  public static Tag createBasicTag(Integer id) {
    Tag tag = new Tag();
    tag.setId(id);
    tag.setUuid(UUID.randomUUID());
    tag.setName("test-tag" + id);
    return tag;
  }

  public static Tag createTag(String name, int id) {
    Tag tag = new Tag();
    tag.setUuid(UUID.randomUUID());
    tag.setName(name);
    tag.setId(id);
    return tag;
  }

  public static Tag createTag(String name) {
    Tag tag = new Tag();
    tag.setUuid(UUID.randomUUID());
    tag.setName(name);
    return tag;
  }

  public static Tag createTag(UUID uuid, String name) {
    Tag tag = new Tag();
    tag.setUuid(uuid);
    tag.setName(name);
    return tag;
  }

  public static DeleteTagDto createDeleteTagDto(@NotEmpty UUID uuid, User user) {
    return new DeleteTagDto(
      uuid,
      AuthenticatedUserFactory.createAuthenticatedUserDto(user)
    );
  }

  public static SaveTagDto createSaveTagDto(@NotNull UUID tagUuid, @NotEmpty String tagName,
    User user) {
    return new SaveTagDto(
      tagUuid,
      tagName,
      AuthenticatedUserFactory.createAuthenticatedUserDto(user)
    );
  }

  public static UpdateTagDto createUpdateTagDto(UUID tagUuid, String tagName, User user) {
    return new UpdateTagDto(
      tagUuid,
      tagName,
      AuthenticatedUserFactory.createAuthenticatedUserDto(user)
    );
  }
}
