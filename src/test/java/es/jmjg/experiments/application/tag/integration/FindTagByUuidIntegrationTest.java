package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.FindTagByUuid;
import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class FindTagByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindTagByUuid findTagByUuid;

  @Test
  void findByUuid_WhenTagExists_ShouldReturnTag() {
    // Given - using existing test data from migration
    UUID existingTagUuid = TestDataSamples.TECHNOLOGY_UUID;

    // When
    Tag result = findTagByUuid.findByUuid(existingTagUuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(TestDataSamples.TECHNOLOGY_TAG_NAME);
    assertThat(result.getUuid()).isEqualTo(existingTagUuid);
  }

  @Test
  void findByUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> findTagByUuid.findByUuid(nonExistentUuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + nonExistentUuid);
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldThrowIllegalArgumentException() {
    // When & Then
    assertThatThrownBy(() -> findTagByUuid.findByUuid(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UUID cannot be null");
  }

  @Test
  void findByUuidOptional_WhenTagExists_ShouldReturnOptionalWithTag() {
    // Given - using existing test data from migration
    UUID existingTagUuid = TestDataSamples.JAVA_UUID;

    // When
    Optional<Tag> result = findTagByUuid.findByUuidOptional(existingTagUuid);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(TestDataSamples.JAVA_TAG_NAME);
    assertThat(result.get().getUuid()).isEqualTo(existingTagUuid);
  }

  @Test
  void findByUuidOptional_WhenTagDoesNotExist_ShouldReturnEmptyOptional() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    Optional<Tag> result = findTagByUuid.findByUuidOptional(nonExistentUuid);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuidOptional_WhenUuidIsNull_ShouldReturnEmptyOptional() {
    // When
    Optional<Tag> result = findTagByUuid.findByUuidOptional(null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuid_WhenMultipleTagsExist_ShouldReturnCorrectTag() {
    // Given - using existing test data from migration
    UUID springBootTagUuid = TestDataSamples.SPRING_BOOT_TAG_UUID;
    UUID databaseTagUuid = TestDataSamples.DATABASE_TAG_UUID;

    // When
    Tag springBootResult = findTagByUuid.findByUuid(springBootTagUuid);
    Tag databaseResult = findTagByUuid.findByUuid(databaseTagUuid);

    // Then
    assertThat(springBootResult).isNotNull();
    assertThat(springBootResult.getName()).isEqualTo(TestDataSamples.SPRING_BOOT_TAG_NAME);
    assertThat(springBootResult.getUuid()).isEqualTo(springBootTagUuid);

    assertThat(databaseResult).isNotNull();
    assertThat(databaseResult.getName()).isEqualTo(TestDataSamples.DATABASE_TAG_NAME);
    assertThat(databaseResult.getUuid()).isEqualTo(databaseTagUuid);
  }
}