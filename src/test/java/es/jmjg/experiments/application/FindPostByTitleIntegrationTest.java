package es.jmjg.experiments.application;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindPostByTitleIntegrationTest extends TestContainersConfig {

    @Autowired
    private FindPostByTitle findPostByTitle;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment environment;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        postRepository.deleteAll();
        userRepository.deleteAll();

        // Create a test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser = userRepository.save(testUser);

        // Create test post associated with the user
        testPost = new Post();
        testPost.setUuid(UUID.randomUUID());
        testPost.setUser(testUser);
        testPost.setTitle("Test Post");
        testPost.setBody("Test Body");
    }

    @Test
    void shouldUseTestProfile() {
        // Verify that the test profile is active
        String[] activeProfiles = environment.getActiveProfiles();
        assertThat(activeProfiles).contains("test");
    }

    @Test
    void connectionEstablished() {
        assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
        assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
    }

    @Test
    void findByTitle_WhenPostExists_ShouldReturnPost() {
        // Given
        Post savedPost = postRepository.save(testPost);

        // When
        Optional<Post> result = findPostByTitle.findByTitle("Test Post");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Post");
        assertThat(result.get().getBody()).isEqualTo("Test Body");
        assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findByTitle_WhenPostDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Post> result = findPostByTitle.findByTitle("Non-existent Post");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByTitle_WhenTitleIsNull_ShouldReturnEmpty() {
        // When
        Optional<Post> result = findPostByTitle.findByTitle(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByTitle_WhenTitleIsEmpty_ShouldReturnEmpty() {
        // When
        Optional<Post> result = findPostByTitle.findByTitle("");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByTitle_WhenTitleIsBlank_ShouldReturnEmpty() {
        // When
        Optional<Post> result = findPostByTitle.findByTitle("   ");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByTitle_WhenTitleHasLeadingTrailingSpaces_ShouldReturnPost() {
        // Given
        Post savedPost = postRepository.save(testPost);

        // When
        Optional<Post> result = findPostByTitle.findByTitle("  Test Post  ");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Post");
        assertThat(result.get().getBody()).isEqualTo("Test Body");
        assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findByTitle_WhenMultiplePostsExist_ShouldReturnCorrectPost() {
        // Given
        Post savedPost1 = postRepository.save(testPost);

        Post testPost2 = new Post();
        testPost2.setUuid(UUID.randomUUID());
        testPost2.setUser(testUser);
        testPost2.setTitle("Another Post");
        testPost2.setBody("Another Body");
        postRepository.save(testPost2);

        // When
        Optional<Post> result = findPostByTitle.findByTitle("Test Post");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Post");
        assertThat(result.get().getBody()).isEqualTo("Test Body");
    }
}
