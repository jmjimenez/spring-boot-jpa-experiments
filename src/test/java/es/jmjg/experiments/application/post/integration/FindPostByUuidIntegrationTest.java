package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import es.jmjg.experiments.application.post.exception.PostNotFound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

class FindPostByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindPostByUuid findPostByUuid;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void findByUuid_WhenPostExists_ShouldReturnPost() {
    // Given
    User testUser = userRepository.save(UserFactory.createBasicUser());
    Post testPost = PostFactory.createBasicPost(testUser);
    postRepository.save(testPost);

    // When
    Post result = findPostByUuid.findByUuid(testPost.getUuid());

    // Then
    assertThat(result.getBody()).isEqualTo(testPost.getBody());
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());
    assertThat(result.getUuid()).isEqualTo(testPost.getUuid());
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
