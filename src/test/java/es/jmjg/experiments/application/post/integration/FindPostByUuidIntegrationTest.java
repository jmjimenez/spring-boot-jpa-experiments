package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class FindPostByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindPostByUuid findPostByUuid;

  @Test
  @Transactional
  void findByUuid_WhenPostExists_ShouldReturnPost() {
    // When
    Post result = findPostByUuid.findByUuid(TestDataSamples.LEANNE_POST_UUID);

    // Then
    assertThat(result.getUuid()).isEqualTo(TestDataSamples.LEANNE_POST_UUID);
    assertThat(result.getTitle()).isEqualTo(TestDataSamples.LEANNE_POST_TITLE);
    assertThat(result.getTags()).hasSize(3);
    assertThat(result.getTags()).extracting("name").containsExactlyInAnyOrder(
        TestDataSamples.TAG_TECHNOLOGY, TestDataSamples.TAG_PROGRAMMING, TestDataSamples.TAG_JAVA);
    assertThat(result.getComments()).hasSize(2);
    assertThat(result.getComments()).extracting("uuid").containsExactlyInAnyOrder(
        TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID, TestDataSamples.COMMENT_LEANNE_POST_BY_CLEMENTINE_UUID);
  }

  @Test
  void findByUuid_WhenPostDoesNotExist_ShouldThrowPostNotFoundException() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    assertThatThrownBy(() -> findPostByUuid.findByUuid(nonExistentUuid))
        .isInstanceOf(PostNotFound.class)
        .hasMessage("Post with UUID " + nonExistentUuid + " not found");
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldThrowIllegalArgumentException() {
    // When
    assertThatThrownBy(() -> findPostByUuid.findByUuid(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UUID cannot be null");
  }
}
