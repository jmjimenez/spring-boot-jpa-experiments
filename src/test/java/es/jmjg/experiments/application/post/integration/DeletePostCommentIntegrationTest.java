package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.jmjg.experiments.application.post.DeletePost;
import es.jmjg.experiments.application.post.dto.DeletePostDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeletePostCommentIntegrationTest extends BaseIntegration {

  @Autowired
  private DeletePost deletePost;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void deleteByUuid_WhenUserIsOwner_ShouldDeletePost() {
    // Given - using existing test data from migration
    User postOwner = userRepository.findByUserUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    Post existingPost = postRepository.findByPostUuid(TestDataSamples.POST_1_UUID).orElseThrow();
    assertThat(postRepository.findById(existingPost.getId())).isPresent();

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(existingPost.getPostUuid(), postOwner);

    // When
    deletePost.delete(deletePostDto);

    // Then
    assertThat(postRepository.findById(existingPost.getId())).isEmpty();
  }

  @Test
  void deleteByUuid_WhenUserIsAdmin_ShouldDeletePost() {
    // Given - using existing test data from migration
    User adminUser = userRepository.findByUsername(TestDataSamples.ADMIN_USERNAME).orElseThrow();
    Post existingPost = postRepository.findByPostUuid(TestDataSamples.POST_16_UUID).orElseThrow();
    assertThat(postRepository.findById(existingPost.getId())).isPresent();

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(existingPost.getPostUuid(), adminUser);

    // When
    deletePost.delete(deletePostDto);

    // Then
    assertThat(postRepository.findById(existingPost.getId())).isEmpty();
  }

  @Test
  void deleteByUuid_WhenUserIsNotOwnerAndNotAdmin_ShouldThrowForbidden() {
    // Given - using existing test data from migration
    User nonOwnerUser = userRepository.findByUserUuid(TestDataSamples.PATRICIA_UUID).orElseThrow();
    Post existingPost = postRepository.findByPostUuid(TestDataSamples.POST_3_UUID).orElseThrow();
    assertThat(postRepository.findById(existingPost.getId())).isPresent();

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(existingPost.getPostUuid(), nonOwnerUser);

    // When & Then
    assertThatThrownBy(() -> deletePost.delete(deletePostDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("You are not the owner of this post");

    // Verify post still exists
    assertThat(postRepository.findById(existingPost.getId())).isPresent();
  }

  @Test
  void deleteByUuid_WhenPostDoesNotExist_ShouldThrowPostNotFoundException() {
    // Given - using existing test data from migration
    User testUser = userRepository.findByUserUuid(TestDataSamples.CHELSEY_UUID).orElseThrow();
    final PostUuid nonExistentUuid = PostUuid.randomUUID();

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(nonExistentUuid, testUser);

    // When & Then
    assertThatThrownBy(() -> deletePost.delete(deletePostDto))
        .isInstanceOf(PostNotFound.class)
        .hasMessage("Post not found with id: " + nonExistentUuid);
  }
}
