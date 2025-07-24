package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.exception.InvalidRequest;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

class SavePostIntegrationTest extends BaseIntegration {

  @Autowired
  private SavePost savePost;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  void save_ShouldSaveAndReturnPost() {
    // Given
    User testUser =
        userRepository.save(UserFactory.createUser("Alice Johnson", "alice@example.com", "alicej"));
    Post newPost = PostFactory.createPost(testUser, "Test Post 1", "Test Body 1");

    // When
    Post result = savePost.save(newPost);

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
  void save_WhenPostHasNoUser_ShouldThrowInvalidRequest() {
    // Given
    Long postCount = postRepository.count();

    // When & Then
    Post newPost = PostFactory.createPostWithoutUser("Test Post 2", "Test Body 2");
    assertThatThrownBy(() -> savePost.save(newPost))
        .isInstanceOf(InvalidRequest.class)
        .hasMessage("Post must have a user");

    // Verify no post was saved
    assertThat(postRepository.count()).isEqualTo(postCount); // Only the post from previous test
  }

  @Test
  void save_WhenUserIdProvidedAndUserExists_ShouldSetUserAndSave() {
    // Given
    User testUser =
        userRepository.save(UserFactory.createUser("Bob Smith", "bob@example.com", "bobsmith"));
    Post newPost = PostFactory.createPostWithoutUser("Test Post 3", "Test Body 3");

    // When
    Post result = savePost.save(newPost, testUser.getUuid());

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
  void save_WhenUserIdProvidedButUserNotFound_ShouldThrowUserNotFound() {
    // Given
    Long postCount = postRepository.count();
    Post newPost = PostFactory.createPostWithoutUser("Test Post 4", "Test Body 4");
    java.util.UUID nonExistentUserUuid = java.util.UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> savePost.save(newPost, nonExistentUserUuid))
        .isInstanceOf(UserNotFound.class)
        .hasMessage("User not found with uuid: " + nonExistentUserUuid);

    // Verify no post was saved
    assertThat(postRepository.count()).isEqualTo(postCount); // Posts from previous tests
  }

  @Test
  void save_WhenUserIdIsNull_ShouldThrowInvalidRequest() {
    // Given
    Long postCount = postRepository.count();
    Post newPost = PostFactory.createPostWithoutUser("Test Post 5", "Test Body 5");

    // When & Then
    assertThatThrownBy(() -> savePost.save(newPost, null))
        .isInstanceOf(InvalidRequest.class)
        .hasMessage("Post must have a user");

    // Verify no post was saved
    assertThat(postRepository.count()).isEqualTo(postCount); // Posts from previous tests
  }

  @Test
  void save_WhenPostAlreadyHasUserAndUserIdProvided_ShouldKeepExistingUser() {
    // Given
    User testUser =
        userRepository.save(UserFactory.createUser("Carol Davis", "carol@example.com", "carold"));
    Post newPost = PostFactory.createPost(testUser, "Test Post 6", "Test Body 6");

    // When
    Post result = savePost.save(newPost, testUser.getUuid());

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
