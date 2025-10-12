package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.domain.tag.entity.TagUuid;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;
import java.util.Optional;

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
    Tag tag = tagRepository.findByTagUuid(TestDataSamples.TAG_TECHNOLOGY_UUID).orElseThrow();
    String newName = "updated-technology";

    // When
    Tag result = updateTag.update(TagFactory.createUpdateTagDto(tag.getTagUuid(), newName, UserFactory.createAdminUser()));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTagUuid()).isEqualTo(tag.getTagUuid());
    assertThat(result.getName()).isEqualTo(newName);

    // Verify the change is persisted in the database
    Optional<Tag> foundTag = tagRepository.findByTagUuid(result.getTagUuid());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(newName);
  }

  @Test
  void updateName_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    var nonExistentUuid = TagUuid.randomUUID();
    String newName = "updated-tag";

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(nonExistentUuid, newName, UserFactory.createAdminUser())))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with id: " + nonExistentUuid);
  }

  @Test
  void updateName_WhenNewNameIsNull_ShouldThrowIllegalArgumentException() {
    // Given
    Tag tag = tagRepository.findByTagUuid(TestDataSamples.TAG_JAVA_UUID).orElseThrow();

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(tag.getTagUuid(), null, UserFactory.createAdminUser())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsEmpty_ShouldThrowIllegalArgumentException() {
    // Given
    Tag tag = tagRepository.findByTagUuid(TestDataSamples.TAG_JAVA_UUID).orElseThrow();

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(tag.getTagUuid(), "", UserFactory.createAdminUser())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsWhitespace_ShouldThrowIllegalArgumentException() {
    // Given
    Tag tag = tagRepository.findByTagUuid(TestDataSamples.TAG_JAVA_UUID).orElseThrow();

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(tag.getTagUuid(), "   ", UserFactory.createAdminUser())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsTrimmed_ShouldWorkCorrectly() {
    // Given
    Tag tag = tagRepository.findByTagUuid(TestDataSamples.TAG_JAVA_UUID).orElseThrow();
    String newName = "  updated-java  ";

    // When
    Tag result = updateTag.update(TagFactory.createUpdateTagDto(tag.getTagUuid(), newName, UserFactory.createAdminUser()));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTagUuid()).isEqualTo(tag.getTagUuid());
    assertThat(result.getName()).isEqualTo(newName.trim());

    // Verify the change is persisted in the database
    Optional<Tag> foundTag = tagRepository.findByTagUuid(tag.getTagUuid());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(newName.trim());
  }
}
