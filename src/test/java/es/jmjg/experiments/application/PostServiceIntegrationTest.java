package es.jmjg.experiments.application;

import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class PostServiceIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private Environment environment;

    private Post testPost1;
    private Post testPost2;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        postRepository.deleteAll();
        
        // Create test data without predefined IDs
        testPost1 = new Post(null, 1, "Test Post 1", "Test Body 1");
        testPost2 = new Post(null, 1, "Test Post 2", "Test Body 2");
    }

    @Test
    void shouldUseTestProfile() {
        // Verify that the test profile is active
        String[] activeProfiles = environment.getActiveProfiles();
        assertThat(activeProfiles).contains("test");
    }

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void findAll_ShouldReturnAllPosts() {
        // Given
        postRepository.save(testPost1);
        postRepository.save(testPost2);

        // When
        List<Post> result = postService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting("title")
                .containsExactlyInAnyOrder("Test Post 1", "Test Post 2");
    }

    @Test
    void findAll_WhenNoPosts_ShouldReturnEmptyList() {
        // When
        List<Post> result = postService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void findById_WhenPostExists_ShouldReturnPost() {
        // Given
        Post savedPost = postRepository.save(testPost1);

        // When
        Optional<Post> result = postService.findById(savedPost.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Post 1");
        assertThat(result.get().getBody()).isEqualTo("Test Body 1");
        assertThat(result.get().getUserId()).isEqualTo(1);
    }

    @Test
    void findById_WhenPostDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Post> result = postService.findById(999);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldSaveAndReturnPost() {
        // Given
        Post newPost = new Post(null, 1, "New Post", "New Body");

        // When
        Post result = postService.save(newPost);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Post");
        assertThat(result.getBody()).isEqualTo("New Body");
        assertThat(result.getUserId()).isEqualTo(1);

        // Verify it was actually saved to the database
        Optional<Post> savedPost = postRepository.findById(result.getId());
        assertThat(savedPost).isPresent();
        assertThat(savedPost.get().getTitle()).isEqualTo("New Post");
    }

    @Test
    void findByTitle_WhenPostExists_ShouldReturnPost() {
        // Given
        postRepository.save(testPost1);

        // When
        Optional<Post> result = postService.findByTitle("Test Post 1");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Post 1");
        assertThat(result.get().getBody()).isEqualTo("Test Body 1");
    }

    @Test
    void findByTitle_WhenPostDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Post> result = postService.findByTitle("Non-existent Post");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_ShouldDeletePost() {
        // Given
        Post savedPost = postRepository.save(testPost1);
        assertThat(postRepository.findById(savedPost.getId())).isPresent();

        // When
        postService.deleteById(savedPost.getId());

        // Then
        assertThat(postRepository.findById(savedPost.getId())).isEmpty();
    }

    @Test
    void deleteById_WhenPostDoesNotExist_ShouldNotThrowException() {
        // When & Then - should not throw any exception
        postService.deleteById(999);
    }

    @Test
    void update_WhenPostExists_ShouldUpdateAndReturnPost() {
        // Given
        Post savedPost = postRepository.save(testPost1);
        Post updateData = new Post(null, 2, "Updated Title", "Updated Body");

        // When
        Post result = postService.update(savedPost.getId(), updateData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedPost.getId());
        assertThat(result.getUserId()).isEqualTo(1); // Should preserve original userId
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getBody()).isEqualTo("Updated Body");

        // Verify it was actually updated in the database
        Optional<Post> updatedPost = postRepository.findById(savedPost.getId());
        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getTitle()).isEqualTo("Updated Title");
        assertThat(updatedPost.get().getBody()).isEqualTo("Updated Body");
    }

    @Test
    void update_WhenPostDoesNotExist_ShouldThrowRuntimeException() {
        // Given
        Post updateData = new Post(null, 1, "Updated Title", "Updated Body");

        // When & Then
        assertThatThrownBy(() -> postService.update(999, updateData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Post not found with id: 999");
    }

    @Test
    void update_ShouldPreserveOriginalIdAndUserId() {
        // Given
        Post originalPost = new Post(null, 5, "Original Title", "Original Body");
        Post savedPost = postRepository.save(originalPost);
        
        Post updateData = new Post(999, 999, "Updated Title", "Updated Body"); // Different ID and userId

        // When
        Post result = postService.update(savedPost.getId(), updateData);

        // Then
        assertThat(result.getId()).isEqualTo(savedPost.getId()); // Should preserve original ID
        assertThat(result.getUserId()).isEqualTo(5); // Should preserve original userId
        assertThat(result.getTitle()).isEqualTo("Updated Title"); // Should update title
        assertThat(result.getBody()).isEqualTo("Updated Body"); // Should update body
    }

    @Test
    void fullCrudWorkflow_ShouldWorkCorrectly() {
        // Create
        Post newPost = new Post(null, 1, "Workflow Post", "Workflow Body");
        Post createdPost = postService.save(newPost);
        assertThat(createdPost.getId()).isNotNull();
        assertThat(createdPost.getTitle()).isEqualTo("Workflow Post");

        // Read
        Optional<Post> foundPost = postService.findById(createdPost.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("Workflow Post");

        // Update
        Post updateData = new Post(null, 2, "Updated Workflow Post", "Updated Workflow Body");
        Post updatedPost = postService.update(createdPost.getId(), updateData);
        assertThat(updatedPost.getTitle()).isEqualTo("Updated Workflow Post");
        assertThat(updatedPost.getUserId()).isEqualTo(1); // Should preserve original userId

        // Delete
        postService.deleteById(createdPost.getId());
        Optional<Post> deletedPost = postService.findById(createdPost.getId());
        assertThat(deletedPost).isEmpty();
    }
}
