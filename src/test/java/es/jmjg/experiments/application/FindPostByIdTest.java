package es.jmjg.experiments.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import es.jmjg.experiments.application.post.FindPostById;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class FindPostByIdTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private FindPostById findPostById;

    private Post testPost;
    private User testUser;
    private UUID testUuid;

    @BeforeEach
    void setUp() {
        testUser = new User(1, "Test User", "test@example.com", "testuser", null);
        testUuid = UUID.randomUUID();
        testPost = new Post(1, testUuid, testUser, "Test Post", "Test Body");
    }

    @Test
    void findById_WhenPostExists_ShouldReturnPost() {
        // Given
        Integer postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        Optional<Post> result = findPostById.findById(postId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testPost);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void findById_WhenPostDoesNotExist_ShouldReturnEmpty() {
        // Given
        Integer postId = 999;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        Optional<Post> result = findPostById.findById(postId);

        // Then
        assertThat(result).isEmpty();
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void findById_WhenIdIsNull_ShouldReturnEmpty() {
        // When
        Optional<Post> result = findPostById.findById(null);

        // Then
        assertThat(result).isEmpty();
        verify(postRepository, never()).findById(any());
    }
}
