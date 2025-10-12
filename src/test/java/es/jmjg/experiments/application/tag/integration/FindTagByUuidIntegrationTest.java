package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.domain.tag.entity.TagUuid;

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
    final var existingTagUuid = TestDataSamples.TAG_TECHNOLOGY_UUID;

    // When
    Tag result = findTagByUuid.findByTagUuid(existingTagUuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(TestDataSamples.TAG_TECHNOLOGY);
    assertThat(result.getTagUuid()).isEqualTo(existingTagUuid);
  }

  @Test
  void findByUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    var nonExistentUuid = TagUuid.randomUUID();

    // When & Then
    assertThatThrownBy(() -> findTagByUuid.findByTagUuid(nonExistentUuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with id: " + nonExistentUuid);
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldThrowIllegalArgumentException() {
    // When & Then
    assertThatThrownBy(() -> findTagByUuid.findByTagUuid(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UUID cannot be null");
  }

  @Test
  void findByUuid_WhenMultipleTagsExist_ShouldReturnCorrectTag() {
    // Given - using existing test data from migration
    var springBootTagUuid = TestDataSamples.TAG_SPRING_BOOT_UUID;
    var databaseTagUuid = TestDataSamples.TAG_DATABASE_UUID;

    // When
    Tag springBootResult = findTagByUuid.findByTagUuid(springBootTagUuid);
    Tag databaseResult = findTagByUuid.findByTagUuid(databaseTagUuid);

    // Then
    assertThat(springBootResult).isNotNull();
    assertThat(springBootResult.getName()).isEqualTo(TestDataSamples.TAG_SPRING_BOOT);
    assertThat(springBootResult.getTagUuid()).isEqualTo(springBootTagUuid);

    assertThat(databaseResult).isNotNull();
    assertThat(databaseResult.getName()).isEqualTo(TestDataSamples.TAG_DATABASE);
    assertThat(databaseResult.getTagUuid()).isEqualTo(databaseTagUuid);
  }
}
