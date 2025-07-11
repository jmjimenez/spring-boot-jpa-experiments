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
import es.jmjg.experiments.application.post.FindPostById;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindPostByIdIntegrationTest extends TestContainersConfig {

    @Autowired
    private FindPostById findPostById;

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
    void findById_WhenPostExists_ShouldReturnPost() {
        // Given
        Post savedPost = postRepository.save(testPost);

        // When
        Optional<Post> result = findPostById.findById(savedPost.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Post");
        assertThat(result.get().getBody()).isEqualTo("Test Body");
        assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findById_WhenPostDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Post> result = findPostById.findById(999);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findById_WhenIdIsNull_ShouldReturnEmpty() {
        // When
        Optional<Post> result = findPostById.findById(null);

        // Then
        assertThat(result).isEmpty();
    }
}
