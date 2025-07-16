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

import es.jmjg.experiments.application.tag.UpdateTagName;
import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.Tag;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.TagFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UpdateTagNameIntegrationTest extends TestContainersConfig {

  @Autowired
  private UpdateTagName updateTagName;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private Environment environment;

  @BeforeEach
  void setUp() {
    // Clean up before each test
    tagRepository.deleteAll();
  }

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
  void updateName_WhenTagExists_ShouldUpdateAndReturnTag() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    Tag savedTag = tagRepository.save(tag);
    String newName = "updated-tag";

    // When
    Tag result = updateTagName.updateName(savedTag.getUuid(), newName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(newName);
    assertThat(result.getUuid()).isEqualTo(savedTag.getUuid());
    assertThat(result.getId()).isEqualTo(savedTag.getId());

    // Verify the change is persisted in the database
    Optional<Tag> foundTag = tagRepository.findByUuid(savedTag.getUuid());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(newName);
  }

  @Test
  void updateName_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    String newName = "updated-tag";

    // When & Then
    assertThatThrownBy(() -> updateTagName.updateName(nonExistentUuid, newName))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + nonExistentUuid);
  }

  @Test
  void updateName_WhenNewNameIsNull_ShouldThrowIllegalArgumentException() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    Tag savedTag = tagRepository.save(tag);

    // When & Then
    assertThatThrownBy(() -> updateTagName.updateName(savedTag.getUuid(), null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsEmpty_ShouldThrowIllegalArgumentException() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    Tag savedTag = tagRepository.save(tag);

    // When & Then
    assertThatThrownBy(() -> updateTagName.updateName(savedTag.getUuid(), ""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsWhitespace_ShouldThrowIllegalArgumentException() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    Tag savedTag = tagRepository.save(tag);

    // When & Then
    assertThatThrownBy(() -> updateTagName.updateName(savedTag.getUuid(), "   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsTrimmed_ShouldWorkCorrectly() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    Tag savedTag = tagRepository.save(tag);
    String newName = "  updated-tag  ";
    String expectedName = "updated-tag";

    // When
    Tag result = updateTagName.updateName(savedTag.getUuid(), newName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(expectedName);
    assertThat(result.getUuid()).isEqualTo(savedTag.getUuid());

    // Verify the change is persisted in the database
    Optional<Tag> foundTag = tagRepository.findByUuid(savedTag.getUuid());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(expectedName);
  }

  @Test
  void updateName_WithCustomTag_ShouldUpdateAndReturnTag() {
    // Given
    Tag tag = TagFactory.createTag("custom-tag");
    Tag savedTag = tagRepository.save(tag);
    String newName = "new-custom-tag";

    // When
    Tag result = updateTagName.updateName(savedTag.getUuid(), newName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(newName);
    assertThat(result.getUuid()).isEqualTo(savedTag.getUuid());

    // Verify the change is persisted in the database
    Optional<Tag> foundTag = tagRepository.findByUuid(savedTag.getUuid());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(newName);
  }
}