package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.UpdatePost;
import es.jmjg.experiments.application.post.dto.UpdatePostDto;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserDetailsFactory;

class UpdatePostIntegrationTest extends BaseIntegration {

  @Autowired
  private UpdatePost updatePost;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void update_ShouldUpdateAndReturnPost() {
    // Given
    User testUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    Post existingPost = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow();

    final String updatedTitle = "Updated Post Title";
    final String updatedBody = "Updated post body content";

    UpdatePostDto updatePostDto = PostFactory.createPostUpdateDto(
        existingPost.getUuid(),
        updatedTitle,
        updatedBody,
        testUser);

    // When
    Post result = updatePost.update(updatePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(existingPost.getId());
    assertThat(result.getUuid()).isEqualTo(existingPost.getUuid());
    assertThat(result.getTitle()).isEqualTo(updatedTitle);
    assertThat(result.getBody()).isEqualTo(updatedBody);
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());

    // Verify it was actually updated in the database
    Optional<Post> updatedPost = postRepository.findById(result.getId());
    assertThat(updatedPost).isPresent();
    assertThat(updatedPost.get().getTitle()).isEqualTo(updatedTitle);
    assertThat(updatedPost.get().getBody()).isEqualTo(updatedBody);
  }

  @Test
  void update_WithNonExistentPost_ShouldThrowPostNotFound() {
    // Given
    User testUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    final String nonExistentUuid = "550e8400-e29b-41d4-a716-446655440999";

    UpdatePostDto updatePostDto = PostFactory.createPostUpdateDto(
        java.util.UUID.fromString(nonExistentUuid),
        "Updated Title",
        "Updated Body",
        testUser);

    // When & Then
    assertThatThrownBy(() -> updatePost.update(updatePostDto))
        .isInstanceOf(PostNotFound.class)
        .hasMessageContaining(nonExistentUuid);
  }

  @Test
  void update_WithNonOwnerUser_ShouldThrowForbidden() {
    // Given
    User nonOwner = userRepository.findByUuid(TestDataSamples.ERVIN_UUID).orElseThrow();
    Post existingPost = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow();

    UpdatePostDto updatePostDto = PostFactory.createPostUpdateDto(
        existingPost.getUuid(),
        "Updated Title",
        "Updated Body",
        nonOwner);

    // When & Then
    assertThatThrownBy(() -> updatePost.update(updatePostDto))
        .isInstanceOf(Forbidden.class)
        .hasMessageContaining("You are not the owner of this post");
  }

  @Test
  void update_WithAdminUser_ShouldUpdateSuccessfully() {
    // Given
    User adminUser = userRepository.findByUuid(TestDataSamples.ADMIN_UUID).orElseThrow();
    Post existingPost = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow();

    final String updatedTitle = "Admin Updated Title";
    final String updatedBody = "Admin updated post body content";

    UpdatePostDto updatePostDto = new UpdatePostDto(
        existingPost.getUuid(),
        updatedTitle,
        updatedBody,
        null,
        UserDetailsFactory.createJwtUserDetails(adminUser));

    // When
    Post result = updatePost.update(updatePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo(updatedTitle);
    assertThat(result.getBody()).isEqualTo(updatedBody);

    // Verify it was actually updated in the database
    Optional<Post> updatedPost = postRepository.findById(result.getId());
    assertThat(updatedPost).isPresent();
    assertThat(updatedPost.get().getTitle()).isEqualTo(updatedTitle);
    assertThat(updatedPost.get().getBody()).isEqualTo(updatedBody);
  }
}
