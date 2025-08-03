package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

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
    Optional<Post> result = findPostByUuid.findByUuid(testPost.getUuid());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(testPost.getTitle());
    assertThat(result.get().getBody()).isEqualTo(testPost.getBody());
    assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
    assertThat(result.get().getUuid()).isEqualTo(testPost.getUuid());
  }

  @Test
  void findByUuid_WhenPostDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByUuid.findByUuid(UUID.randomUUID());

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByUuid.findByUuid(null);

    // Then
    assertThat(result).isEmpty();
  }
}
