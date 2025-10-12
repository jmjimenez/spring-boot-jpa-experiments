package es.jmjg.experiments.shared;

import es.jmjg.experiments.application.tag.dto.DeleteTagDto;
import es.jmjg.experiments.application.tag.dto.SaveTagDto;
import es.jmjg.experiments.application.tag.dto.UpdateTagDto;
import es.jmjg.experiments.domain.tag.entity.TagUuid;
import es.jmjg.experiments.domain.user.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import es.jmjg.experiments.domain.tag.entity.Tag;

public class TagFactory {

  public static Tag createBasicTag() {
    Tag tag = new Tag();
    tag.setTagUuid(TagUuid.randomUUID());
    tag.setName("test-tag");
    return tag;
  }

  public static Tag createBasicTag(Integer id) {
    Tag tag = new Tag();
    tag.setId(id);
    tag.setTagUuid(TagUuid.randomUUID());
    tag.setName("test-tag" + id);
    return tag;
  }

  public static Tag createTag(String name, int id) {
    Tag tag = new Tag();
    tag.setTagUuid(TagUuid.randomUUID());
    tag.setName(name);
    tag.setId(id);
    return tag;
  }

  public static Tag createTag(String name) {
    Tag tag = new Tag();
    tag.setTagUuid(TagUuid.randomUUID());
    tag.setName(name);
    return tag;
  }

  public static Tag createTag(TagUuid uuid, String name) {
    Tag tag = new Tag();
    tag.setTagUuid(uuid);
    tag.setName(name);
    return tag;
  }

  public static DeleteTagDto createDeleteTagDto(@NotEmpty TagUuid uuid, User user) {
    return new DeleteTagDto(
      uuid,
      AuthenticatedUserFactory.createAuthenticatedUserDto(user)
    );
  }

  public static SaveTagDto createSaveTagDto(@NotNull TagUuid tagUuid, @NotEmpty String tagName,
    User user) {
    return new SaveTagDto(
      tagUuid,
      tagName,
      AuthenticatedUserFactory.createAuthenticatedUserDto(user)
    );
  }

  public static UpdateTagDto createUpdateTagDto(TagUuid tagUuid, String tagName, User user) {
    return new UpdateTagDto(
      tagUuid,
      tagName,
      AuthenticatedUserFactory.createAuthenticatedUserDto(user)
    );
  }
}
