package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.DeletePost;
import es.jmjg.experiments.application.post.dto.DeletePostDto;
import es.jmjg.experiments.application.post.exception.Forbidden;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TestDataSamples;

class DeletePostIntegrationTest extends BaseIntegration {

  @Autowired
  private DeletePost deletePost;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void deleteByUuid_WhenUserIsOwner_ShouldDeletePost() {
    // Given - using existing test data from migration
    User postOwner = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    Post existingPost = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow();
    assertThat(postRepository.findById(existingPost.getId())).isPresent();

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(existingPost.getUuid(), postOwner);

    // When
    deletePost.delete(deletePostDto);

    // Then
    assertThat(postRepository.findById(existingPost.getId())).isEmpty();
  }

  @Test
  void deleteByUuid_WhenUserIsAdmin_ShouldDeletePost() {
    // Given - using existing test data from migration
    User adminUser = userRepository.findByUsername(TestDataSamples.ADMIN_USERNAME).orElseThrow();
    Post existingPost = postRepository.findByUuid(TestDataSamples.POST_16_UUID).orElseThrow();
    assertThat(postRepository.findById(existingPost.getId())).isPresent();

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(existingPost.getUuid(), adminUser);

    // When
    deletePost.delete(deletePostDto);

    // Then
    assertThat(postRepository.findById(existingPost.getId())).isEmpty();
  }

  @Test
  void deleteByUuid_WhenUserIsNotOwnerAndNotAdmin_ShouldThrowForbidden() {
    // Given - using existing test data from migration
    User nonOwnerUser = userRepository.findByUuid(TestDataSamples.PATRICIA_UUID).orElseThrow();
    Post existingPost = postRepository.findByUuid(TestDataSamples.POST_3_UUID).orElseThrow();
    assertThat(postRepository.findById(existingPost.getId())).isPresent();

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(existingPost.getUuid(), nonOwnerUser);

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
    User testUser = userRepository.findByUuid(TestDataSamples.CHELSEY_UUID).orElseThrow();
    final UUID nonExistentUuid = UUID.randomUUID();

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(nonExistentUuid, testUser);

    // When & Then
    assertThatThrownBy(() -> deletePost.delete(deletePostDto))
        .isInstanceOf(es.jmjg.experiments.application.post.exception.PostNotFound.class)
        .hasMessage("Post not found with uuid: " + nonExistentUuid);
  }

  @Test
  void deleteByUuid_WhenUserIsNotAuthenticated_ShouldThrowNullPointerException() {
    // Given - using existing test data from migration
    Post existingPost = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow();
    assertThat(postRepository.findById(existingPost.getId())).isPresent();

    final DeletePostDto deletePostDto = new DeletePostDto(existingPost.getUuid(), null);

    // When & Then
    assertThatThrownBy(() -> deletePost.delete(deletePostDto))
        .isInstanceOf(NullPointerException.class);

    // Verify post still exists
    assertThat(postRepository.findById(existingPost.getId())).isPresent();
  }
}
