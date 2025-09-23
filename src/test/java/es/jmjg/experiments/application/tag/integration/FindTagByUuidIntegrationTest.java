package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.FindTagByUuid;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class FindTagByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindTagByUuid findTagByUuid;

  @Test
  void findByUuid_WhenTagExists_ShouldReturnTag() {
    // Given - using existing test data from migration
    UUID existingTagUuid = TestDataSamples.TAG_TECHNOLOGY_UUID;

    // When
    Tag result = findTagByUuid.findByUuid(existingTagUuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(TestDataSamples.TAG_TECHNOLOGY);
    assertThat(result.getUuid()).isEqualTo(existingTagUuid);
  }

  @Test
  void findByUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> findTagByUuid.findByUuid(nonExistentUuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with id: " + nonExistentUuid);
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
    UUID existingTagUuid = TestDataSamples.TAG_JAVA_UUID;

    // When
    Optional<Tag> result = findTagByUuid.findByUuidOptional(existingTagUuid);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(TestDataSamples.TAG_JAVA);
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
    UUID springBootTagUuid = TestDataSamples.TAG_SPRING_BOOT_UUID;
    UUID databaseTagUuid = TestDataSamples.TAG_DATABASE_UUID;

    // When
    Tag springBootResult = findTagByUuid.findByUuid(springBootTagUuid);
    Tag databaseResult = findTagByUuid.findByUuid(databaseTagUuid);

    // Then
    assertThat(springBootResult).isNotNull();
    assertThat(springBootResult.getName()).isEqualTo(TestDataSamples.TAG_SPRING_BOOT);
    assertThat(springBootResult.getUuid()).isEqualTo(springBootTagUuid);

    assertThat(databaseResult).isNotNull();
    assertThat(databaseResult.getName()).isEqualTo(TestDataSamples.TAG_DATABASE);
    assertThat(databaseResult.getUuid()).isEqualTo(databaseTagUuid);
  }
}
