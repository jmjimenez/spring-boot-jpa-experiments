package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.SavePostDto;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

class SavePostIntegrationTest extends BaseIntegration {

  @Autowired
  private SavePost savePost;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void save_ShouldSaveAndReturnPost() {
    // Given
    User testUser = userRepository.save(UserFactory.createUser("Alice Johnson", "alice@example.com", "alicej"));
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, "Test Post 1", "Test Body 1");

    // When
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Test Post 1");
    assertThat(result.getBody()).isEqualTo("Test Body 1");
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());
    assertThat(result.getTags()).isNotNull(); // Tags field should be present

    // Verify it was actually saved to the database
    Optional<Post> savedPost = postRepository.findById(result.getId());
    assertThat(savedPost).isPresent();
    assertThat(savedPost.get().getTitle()).isEqualTo("Test Post 1");
    assertThat(savedPost.get().getTags()).isNotNull(); // Tags field should be present
  }

  @Test
  void save_WhenUserIdProvidedAndUserExists_ShouldSetUserAndSave() {
    // Given
    User testUser = userRepository.save(UserFactory.createUser("Bob Smith", "bob@example.com", "bobsmith"));
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, "Test Post 3", "Test Body 3");

    // When
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Test Post 3");
    assertThat(result.getBody()).isEqualTo("Test Body 3");
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());
    assertThat(result.getTags()).isNotNull(); // Tags field should be present

    // Verify it was actually saved to the database
    Optional<Post> savedPost = postRepository.findById(result.getId());
    assertThat(savedPost).isPresent();
    assertThat(savedPost.get().getUser().getId()).isEqualTo(testUser.getId());
    assertThat(savedPost.get().getTags()).isNotNull(); // Tags field should be present
  }

  @Test
  void save_WhenPostAlreadyHasUserAndUserIdProvided_ShouldKeepExistingUser() {
    // Given
    User testUser = userRepository.save(UserFactory.createUser("Carol Davis", "carol@example.com", "carold"));
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, "Test Post 6", "Test Body 6");

    // When
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Test Post 6");
    assertThat(result.getBody()).isEqualTo("Test Body 6");
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());
    assertThat(result.getTags()).isNotNull(); // Tags field should be present

    // Verify it was actually saved to the database
    Optional<Post> savedPost = postRepository.findById(result.getId());
    assertThat(savedPost).isPresent();
    assertThat(savedPost.get().getUser().getId()).isEqualTo(testUser.getId());
    assertThat(savedPost.get().getTags()).isNotNull(); // Tags field should be present
  }
}
