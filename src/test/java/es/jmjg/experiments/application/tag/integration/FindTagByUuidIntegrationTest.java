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

class FindTagByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindTagByUuid findTagByUuid;

  // Sample tags from Flyway test migration data
  private static final UUID TECHNOLOGY_TAG_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440056");
  private static final String TECHNOLOGY_TAG_NAME = "technology";

  private static final UUID JAVA_TAG_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440058");
  private static final String JAVA_TAG_NAME = "java";

  private static final UUID SPRING_BOOT_TAG_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440059");
  private static final String SPRING_BOOT_TAG_NAME = "spring-boot";

  private static final UUID DATABASE_TAG_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440061");
  private static final String DATABASE_TAG_NAME = "database";

  @Test
  void findByUuid_WhenTagExists_ShouldReturnTag() {
    // Given - using existing test data from migration
    UUID existingTagUuid = TECHNOLOGY_TAG_UUID;

    // When
    Tag result = findTagByUuid.findByUuid(existingTagUuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(TECHNOLOGY_TAG_NAME);
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
    UUID existingTagUuid = JAVA_TAG_UUID;

    // When
    Optional<Tag> result = findTagByUuid.findByUuidOptional(existingTagUuid);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(JAVA_TAG_NAME);
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
    UUID springBootTagUuid = SPRING_BOOT_TAG_UUID;
    UUID databaseTagUuid = DATABASE_TAG_UUID;

    // When
    Tag springBootResult = findTagByUuid.findByUuid(springBootTagUuid);
    Tag databaseResult = findTagByUuid.findByUuid(databaseTagUuid);

    // Then
    assertThat(springBootResult).isNotNull();
    assertThat(springBootResult.getName()).isEqualTo(SPRING_BOOT_TAG_NAME);
    assertThat(springBootResult.getUuid()).isEqualTo(springBootTagUuid);

    assertThat(databaseResult).isNotNull();
    assertThat(databaseResult.getName()).isEqualTo(DATABASE_TAG_NAME);
    assertThat(databaseResult.getUuid()).isEqualTo(databaseTagUuid);
  }
}