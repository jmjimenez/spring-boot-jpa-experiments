package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.tag.DeleteTagByUuid;
import es.jmjg.experiments.application.tag.exception.TagInUseException;
import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.Tag;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.TagFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeleteTagByUuidIntegrationTest extends TestContainersConfig {

  @Autowired
  private DeleteTagByUuid deleteTagByUuid;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private Environment environment;

  @Test
  void shouldUseTestProfile() {
    // Verify that the test profile is active
    String[] activeProfiles = environment.getActiveProfiles();
    assertThat(activeProfiles).contains("test");
  }

  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

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
    // Given - Create a tag that is used in posts (from test data)
    // The test data includes tags that are used in posts
    var technologyTag = tagRepository.findByName("technology");
    assertThat(technologyTag).isPresent();

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(technologyTag.get().getUuid()))
        .isInstanceOf(TagInUseException.class)
        .hasMessageContaining("because it is currently in use");
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInUsers_ShouldThrowTagInUseException() {
    // Given - Create a tag that is used in users (from test data)
    // The test data includes tags that are used in users
    var technologyTag = tagRepository.findByName("technology");
    assertThat(technologyTag).isPresent();

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(technologyTag.get().getUuid()))
        .isInstanceOf(TagInUseException.class)
        .hasMessageContaining("because it is currently in use");
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