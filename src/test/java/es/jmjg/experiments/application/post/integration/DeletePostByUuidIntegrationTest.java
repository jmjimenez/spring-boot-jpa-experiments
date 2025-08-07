package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.DeletePostByUuid;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

class DeletePostByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private DeletePostByUuid deletePostByUuid;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void deleteByUuid_ShouldDeletePost() {
    // Given
    User testUser = userRepository.save(UserFactory.createBasicUser());
    Post testPost1 = PostFactory.createBasicPost(testUser);
    Post savedPost = postRepository.save(testPost1);
    assertThat(postRepository.findById(savedPost.getId())).isPresent();

    // When
    deletePostByUuid.deleteByUuid(savedPost.getUuid());

    // Then
    assertThat(postRepository.findById(savedPost.getId())).isEmpty();
  }

  @Test
  void deleteById_WhenPostDoesNotExist_ShouldThrowPostNotFoundException() {
    // Given
    final UUID nonExistentUuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> deletePostByUuid.deleteByUuid(nonExistentUuid))
        .isInstanceOf(es.jmjg.experiments.application.post.exception.PostNotFound.class)
        .hasMessage("Post not found with uuid: " + nonExistentUuid);
  }
}
