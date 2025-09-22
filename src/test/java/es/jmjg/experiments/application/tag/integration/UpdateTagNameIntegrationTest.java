package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.UpdateTag;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.TagRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TagFactory;

class UpdateTagNameIntegrationTest extends BaseIntegration {

  @Autowired
  private UpdateTag updateTag;

  @Autowired
  private TagRepositoryImpl tagRepository;

  @Test
  void updateName_WhenTagExists_ShouldUpdateAndReturnTag() {
    // Given
    Tag tag = tagRepository.findByUuid(TestDataSamples.TAG_TECHNOLOGY_UUID).orElseThrow();
    String newName = "updated-technology";

    // When
    Tag result = updateTag.update(TagFactory.createUpdateTagDto(tag.getUuid(), newName, UserFactory.createAdminUser()));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUuid()).isEqualTo(tag.getUuid());
    assertThat(result.getName()).isEqualTo(newName);

    // Verify the change is persisted in the database
    Optional<Tag> foundTag = tagRepository.findByUuid(result.getUuid());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(newName);
  }

  @Test
  void updateName_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    String newName = "updated-tag";

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(nonExistentUuid, newName, UserFactory.createAdminUser())))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + nonExistentUuid);
  }

  @Test
  void updateName_WhenNewNameIsNull_ShouldThrowIllegalArgumentException() {
    // Given
    Tag tag = tagRepository.findByUuid(TestDataSamples.TAG_JAVA_UUID).orElseThrow();

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(tag.getUuid(), null, UserFactory.createAdminUser())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsEmpty_ShouldThrowIllegalArgumentException() {
    // Given
    Tag tag = tagRepository.findByUuid(TestDataSamples.TAG_JAVA_UUID).orElseThrow();

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(tag.getUuid(), "", UserFactory.createAdminUser())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsWhitespace_ShouldThrowIllegalArgumentException() {
    // Given
    Tag tag = tagRepository.findByUuid(TestDataSamples.TAG_JAVA_UUID).orElseThrow();

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(tag.getUuid(), "   ", UserFactory.createAdminUser())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsTrimmed_ShouldWorkCorrectly() {
    // Given
    Tag tag = tagRepository.findByUuid(TestDataSamples.TAG_JAVA_UUID).orElseThrow();
    String newName = "  updated-java  ";

    // When
    Tag result = updateTag.update(TagFactory.createUpdateTagDto(tag.getUuid(), newName, UserFactory.createAdminUser()));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUuid()).isEqualTo(tag.getUuid());
    assertThat(result.getName()).isEqualTo(newName.trim());

    // Verify the change is persisted in the database
    Optional<Tag> foundTag = tagRepository.findByUuid(tag.getUuid());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(newName.trim());
  }
}
