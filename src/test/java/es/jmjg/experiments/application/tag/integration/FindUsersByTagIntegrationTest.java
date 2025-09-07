package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.FindUsersByTag;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.TagRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;

class FindUsersByTagIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUsersByTag findUsersByTag;

  @Autowired
  private TagRepositoryImpl tagRepository;

  @Test
  void findByTagUuid_WhenTagExistsAndHasUsers_ShouldReturnUsers() {
    // Given - Use existing test data that has users with tags
    var technologyTag = tagRepository.findByName("technology");
    assertThat(technologyTag).isPresent();

    // When
    List<User> result = findUsersByTag.findByTagUuid(technologyTag.get().getUuid());

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has multiple users with the technology tag
  }

  @Test
  void findByTagUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    var nonExistentUuid = java.util.UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> findUsersByTag.findByTagUuid(nonExistentUuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + nonExistentUuid);
  }

  @Test
  void findByTagName_WhenTagExistsAndHasUsers_ShouldReturnUsers() {
    // Given - Use existing test data that has users with tags
    String tagName = "technology";

    // When
    List<User> result = findUsersByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has multiple users with the technology tag
  }

  @Test
  void findByTagName_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    String tagName = "nonexistent-tag";

    // When & Then
    assertThatThrownBy(() -> findUsersByTag.findByTagName(tagName))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with name: " + tagName);
  }

  @Test
  void findByTagName_WhenTagNameIsNull_ShouldReturnEmptyList() {
    // When
    List<User> result = findUsersByTag.findByTagName(null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsEmpty_ShouldReturnEmptyList() {
    // When
    List<User> result = findUsersByTag.findByTagName("");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsWhitespace_ShouldReturnEmptyList() {
    // When
    List<User> result = findUsersByTag.findByTagName("   ");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsTrimmed_ShouldWorkCorrectly() {
    // Given - Use existing test data that has users with tags
    String tagName = "  technology  ";

    // When
    List<User> result = findUsersByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has multiple users with the technology tag
  }

  @Test
  void findByTagName_WithJavaTag_ShouldReturnUsers() {
    // Given - Use existing test data that has users with java tag
    String tagName = "java";

    // When
    List<User> result = findUsersByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has users with the java tag
  }

  @Test
  void findByTagName_WithProgrammingTag_ShouldReturnUsers() {
    // Given - Use existing test data that has users with programming tag
    String tagName = "programming";

    // When
    List<User> result = findUsersByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has users with the programming tag
  }

  @Test
  void findByTagName_WithDatabaseTag_ShouldReturnUsers() {
    // Given - Use existing test data that has users with database tag
    String tagName = "database";

    // When
    List<User> result = findUsersByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has users with the database tag
  }
}
