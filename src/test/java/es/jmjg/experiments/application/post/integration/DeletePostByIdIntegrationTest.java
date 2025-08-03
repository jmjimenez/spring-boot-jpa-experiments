package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.DeletePostById;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

class DeletePostByIdIntegrationTest extends BaseIntegration {

  @Autowired
  private DeletePostById deletePostById;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void deleteById_ShouldDeletePost() {
    // Given
    User testUser = userRepository.save(UserFactory.createBasicUser());
    Post testPost1 = PostFactory.createBasicPost(testUser);
    Post savedPost = postRepository.save(testPost1);
    assertThat(postRepository.findById(savedPost.getId())).isPresent();

    // When
    deletePostById.deleteById(savedPost.getId());

    // Then
    assertThat(postRepository.findById(savedPost.getId())).isEmpty();
  }

  @Test
  void deleteById_WhenPostDoesNotExist_ShouldNotThrowException() {
    // When & Then - should not throw any exception
    deletePostById.deleteById(999);
  }
}
