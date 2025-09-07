package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.FindPostsByTag;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.infrastructure.repository.TagRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;

class FindPostsByTagIntegrationTest extends BaseIntegration {

  @Autowired
  private FindPostsByTag findPostsByTag;

  @Autowired
  private TagRepositoryImpl tagRepository;

  @Test
  void findByTagUuid_WhenTagExistsAndHasPosts_ShouldReturnPosts() {
    // Given - Use existing test data that has posts with tags
    var technologyTag = tagRepository.findByName("technology");
    assertThat(technologyTag).isPresent();

    // When
    List<Post> result = findPostsByTag.findByTagUuid(technologyTag.get().getUuid());

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has multiple posts with the technology tag
  }

  @Test
  void findByTagUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    var nonExistentUuid = java.util.UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> findPostsByTag.findByTagUuid(nonExistentUuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + nonExistentUuid);
  }

  @Test
  void findByTagName_WhenTagExistsAndHasPosts_ShouldReturnPosts() {
    // Given - Use existing test data that has posts with tags
    String tagName = "technology";

    // When
    List<Post> result = findPostsByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has multiple posts with the technology tag
  }

  @Test
  void findByTagName_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    String tagName = "nonexistent-tag";

    // When & Then
    assertThatThrownBy(() -> findPostsByTag.findByTagName(tagName))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with name: " + tagName);
  }

  @Test
  void findByTagName_WhenTagNameIsNull_ShouldReturnEmptyList() {
    // When
    List<Post> result = findPostsByTag.findByTagName(null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsEmpty_ShouldReturnEmptyList() {
    // When
    List<Post> result = findPostsByTag.findByTagName("");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsWhitespace_ShouldReturnEmptyList() {
    // When
    List<Post> result = findPostsByTag.findByTagName("   ");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsTrimmed_ShouldWorkCorrectly() {
    // Given - Use existing test data that has posts with tags
    String tagName = "  technology  ";

    // When
    List<Post> result = findPostsByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has multiple posts with the technology tag
  }

  @Test
  void findByTagName_WithJavaTag_ShouldReturnPosts() {
    // Given - Use existing test data that has posts with java tag
    String tagName = "java";

    // When
    List<Post> result = findPostsByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has posts with the java tag
  }

  @Test
  void findByTagName_WithSpringBootTag_ShouldReturnPosts() {
    // Given - Use existing test data that has posts with spring-boot tag
    String tagName = "spring-boot";

    // When
    List<Post> result = findPostsByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has posts with the spring-boot tag
  }
}
