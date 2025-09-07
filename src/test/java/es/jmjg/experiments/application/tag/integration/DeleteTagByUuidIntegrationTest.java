package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.DeleteTag;
import es.jmjg.experiments.domain.tag.exception.TagInUseException;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.TagRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TagFactory;
import org.springframework.transaction.annotation.Transactional;

class DeleteTagByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private DeleteTag deleteTag;

  @Autowired
  private TagRepositoryImpl tagRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  @Transactional
  void deleteByUuid_WhenTagExistsAndNotInUse_ShouldDeleteTag() {
    // Given
    User adminUser = userRepository.findByUsername(TestDataSamples.ADMIN_USERNAME).orElseThrow();
    Tag tag = TagFactory.createBasicTag();
    Tag existingTag = tagRepository.save(tag);

    // When
    deleteTag.delete(TagFactory.createDeleteTagDto(existingTag.getUuid(), adminUser));

    // Then
    Optional<Tag> foundTag = tagRepository.findByUuid(existingTag.getUuid());
    assertThat(foundTag).isEmpty();
  }

  @Test
  void deleteByUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    User adminUser = userRepository.findByUsername(TestDataSamples.ADMIN_USERNAME).orElseThrow();
    UUID nonExistentUuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> deleteTag.delete(TagFactory.createDeleteTagDto(nonExistentUuid, adminUser)))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + nonExistentUuid);
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInPosts_ShouldThrowTagInUseException() {
    // Given - Use a tag that is used in posts (from test data)
    User adminUser = userRepository.findByUsername(TestDataSamples.ADMIN_USERNAME).orElseThrow();
    var javaTag = tagRepository.findByName(TestDataSamples.TAG_JAVA).orElseThrow();

    // When & Then
    assertThatThrownBy(() -> deleteTag.delete(TagFactory.createDeleteTagDto(javaTag.getUuid(), adminUser)))
        .isInstanceOf(TagInUseException.class)
        .hasMessageContaining("Cannot delete tag 'java' because it is assigned to posts");
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInUsers_ShouldThrowTagInUseException() {
    // Given - Use the "developer" tag which is assigned to users but not to posts
    User adminUser = userRepository.findByUsername(TestDataSamples.ADMIN_USERNAME).orElseThrow();
    var developerTag = tagRepository.findByName(TestDataSamples.TAG_DEVELOPER ).orElseThrow();

    // When & Then
    assertThatThrownBy(() -> deleteTag.delete(TagFactory.createDeleteTagDto(developerTag.getUuid(), adminUser)))
        .isInstanceOf(TagInUseException.class)
        .hasMessageContaining("Cannot delete tag 'developer' because it is assigned to users");
  }
}
