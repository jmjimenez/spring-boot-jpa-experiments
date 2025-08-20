package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.FindPostByTitle;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class FindPostByTitleIntegrationTest extends BaseIntegration {

  @Autowired
  private FindPostByTitle findPostByTitle;

  @Test
  @Transactional
  void findByTitle_WhenTitleExists_ShouldReturnPost() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle(TestDataSamples.POST_1_TITLE);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(TestDataSamples.POST_1_TITLE);
    assertThat(result.get().getBody()).isEqualTo(TestDataSamples.POST_1_BODY);
    assertThat(result.get().getUser()).isNotNull();
    assertThat(result.get().getUser().getName()).isEqualTo("Leanne Graham");
  }

  @Test
  void findByTitle_WhenTitleDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle("Non-existent Post");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsNull_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle(null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsEmpty_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle("");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsWhitespace_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle("   ");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsUnique_ShouldReturnPost() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle(TestDataSamples.ERVIN_POST_TITLE);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(TestDataSamples.ERVIN_POST_TITLE);
    assertThat(result.get().getBody()).isEqualTo(TestDataSamples.ERVIN_POST_BODY);
    assertThat(result.get().getUser()).isNotNull();
    assertThat(result.get().getUser().getName()).isEqualTo("Ervin Howell");
  }

  @Test
  void findByTitle_WhenTitleExistsForDifferentUser_ShouldReturnPost() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle(TestDataSamples.CLEMENTINE_POST_TITLE);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(TestDataSamples.CLEMENTINE_POST_TITLE);
    assertThat(result.get().getBody()).isEqualTo(TestDataSamples.CLEMENTINE_POST_BODY);
    assertThat(result.get().getUser()).isNotNull();
    assertThat(result.get().getUser().getName()).isEqualTo("Clementine Bauch");
  }
}
