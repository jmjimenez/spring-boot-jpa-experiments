package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.DeleteTagByUuid;
import es.jmjg.experiments.application.tag.exception.TagInUseException;
import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TagFactory;

class DeleteTagByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private DeleteTagByUuid deleteTagByUuid;

  @Autowired
  private TagRepository tagRepository;

  @Test
  void deleteByUuid_WhenTagExistsAndNotInUse_ShouldDeleteTag() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    Tag savedTag = tagRepository.save(tag);

    // When
    deleteTagByUuid.deleteByUuid(savedTag.getUuid());

    // Then
    Optional<Tag> foundTag = tagRepository.findByUuid(savedTag.getUuid());
    assertThat(foundTag).isEmpty();
  }

  @Test
  void deleteByUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(nonExistentUuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + nonExistentUuid);
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInPosts_ShouldThrowTagInUseException() {
    // Given - Use a tag that is used in posts (from test data)
    var javaTag = tagRepository.findByName("java");
    assertThat(javaTag).isPresent();

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(javaTag.get().getUuid()))
        .isInstanceOf(TagInUseException.class)
        .hasMessageContaining("Cannot delete tag 'java' because it is assigned to posts");
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInUsers_ShouldThrowTagInUseException() {
    // Given - Use the "developer" tag which is assigned to users but not to posts
    var developerTag = tagRepository.findByName("developer");
    assertThat(developerTag).isPresent();

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(developerTag.get().getUuid()))
        .isInstanceOf(TagInUseException.class)
        .hasMessageContaining("Cannot delete tag 'developer' because it is assigned to users");
  }

  @Test
  void deleteByUuid_WhenTagIsNotUsed_ShouldDeleteSuccessfully() {
    // Given - Create a new tag that is not used anywhere
    Tag unusedTag = TagFactory.createTag("unused-tag");
    Tag savedTag = tagRepository.save(unusedTag);

    // When
    deleteTagByUuid.deleteByUuid(savedTag.getUuid());

    // Then
    Optional<Tag> foundTag = tagRepository.findByUuid(savedTag.getUuid());
    assertThat(foundTag).isEmpty();
  }
}