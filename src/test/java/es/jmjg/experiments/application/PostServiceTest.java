package es.jmjg.experiments.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost1;
    private Post testPost2;
    private List<Post> testPosts;
    private User testUser;
    private UUID testUuid1;
    private UUID testUuid2;

    @BeforeEach
    void setUp() {
        testUser = new User(1, "Test User", "test@example.com", "testuser", null);
        testUuid1 = UUID.randomUUID();
        testUuid2 = UUID.randomUUID();
        testPost1 = new Post(1, testUuid1, testUser, "Test Post 1", "Test Body 1");
        testPost2 = new Post(2, testUuid2, testUser, "Test Post 2", "Test Body 2");
        testPosts = Arrays.asList(testPost1, testPost2);
    }

    @Test
    void findAll_ShouldReturnAllPosts() {
        // Given
        when(postRepository.findAll()).thenReturn(testPosts);

        // When
        List<Post> result = postService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testPost1, testPost2);
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenPostExists_ShouldReturnPost() {
        // Given
        Integer postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost1));

        // When
        Optional<Post> result = postService.findById(postId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testPost1);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void findById_WhenPostDoesNotExist_ShouldReturnEmpty() {
        // Given
        Integer postId = 999;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        Optional<Post> result = postService.findById(postId);

        // Then
        assertThat(result).isEmpty();
        verify(postRepository, times(1)).findById(postId);
    }



    @Test
    void findByTitle_WhenPostExists_ShouldReturnPost() {
        // Given
        String title = "Test Post 1";
        when(postRepository.findByTitle(title)).thenReturn(Optional.of(testPost1));

        // When
        Optional<Post> result = postService.findByTitle(title);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testPost1);
        verify(postRepository, times(1)).findByTitle(title);
    }

    @Test
    void findByTitle_WhenPostDoesNotExist_ShouldReturnEmpty() {
        // Given
        String title = "Non-existent Post";
        when(postRepository.findByTitle(title)).thenReturn(Optional.empty());

        // When
        Optional<Post> result = postService.findByTitle(title);

        // Then
        assertThat(result).isEmpty();
        verify(postRepository, times(1)).findByTitle(title);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        // Given
        Integer postId = 1;
        doNothing().when(postRepository).deleteById(postId);

        // When
        postService.deleteById(postId);

        // Then
        verify(postRepository, times(1)).deleteById(postId);
    }



}
