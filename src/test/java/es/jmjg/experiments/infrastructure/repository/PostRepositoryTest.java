package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import es.jmjg.experiments.TestContainersConfig;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PostRepositoryTest extends TestContainersConfig {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User(null, "Test User", "test@example.com", "testuser", null);
        user = userRepository.save(user);
        UUID postUuid = UUID.randomUUID();
        List<Post> posts =
                List.of(new Post(null, postUuid, user, "Hello, World!", "This is my first post!"));
        postRepository.saveAll(posts);
    }

    @Test
    void connectionEstablished() {
        assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
        assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
    }

    @Test
    void shouldReturnPostByTitle() {
        Post post = postRepository.findByTitle("Hello, World!").orElseThrow();
        assertEquals("Hello, World!", post.getTitle(), "Post title should be 'Hello, World!'");
    }

    @Test
    void shouldNotReturnPostWhenTitleIsNotFound() {
        Optional<Post> post = postRepository.findByTitle("Hello, Wrong Title!");
        assertFalse(post.isPresent(), "Post should not be present");
    }
}
