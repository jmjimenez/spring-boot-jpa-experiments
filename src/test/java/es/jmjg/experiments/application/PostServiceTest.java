package es.jmjg.experiments.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import es.jmjg.experiments.application.exception.PostNotFound;
import es.jmjg.experiments.application.exception.UserNotFound;
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

    @BeforeEach
    void setUp() {
        testUser = new User(1, "Test User", "test@example.com", "testuser", null);
        testPost1 = new Post(1, testUser, "Test Post 1", "Test Body 1");
        testPost2 = new Post(2, testUser, "Test Post 2", "Test Body 2");
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
    void save_ShouldSaveAndReturnPost() {
        // Given
        Post newPost = new Post(null, testUser, "New Post", "New Body");
        Post savedPost = new Post(3, testUser, "New Post", "New Body");

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // When
        Post result = postService.save(newPost);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getTitle()).isEqualTo("New Post");
        assertThat(result.getBody()).isEqualTo("New Body");
        verify(postRepository, times(1)).save(newPost);
    }

    @Test
    void save_WhenPostAlreadyHasUser_ShouldNotFetchUser() {
        // Given
        Post newPost = new Post(null, testUser, "New Post", "New Body");
        Post savedPost = new Post(3, testUser, "New Post", "New Body");

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // When
        Post result = postService.save(newPost);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3);
        verify(userRepository, never()).findById(any());
        verify(postRepository, times(1)).save(newPost);
    }

    @Test
    void save_WhenUserIdIsNull_ShouldNotFetchUser() {
        // Given
        Post newPost = new Post(null, null, "New Post", "New Body");
        Post savedPost = new Post(3, null, "New Post", "New Body");

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // When
        Post result = postService.save(newPost);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3);
        verify(userRepository, never()).findById(any());
        verify(postRepository, times(1)).save(newPost);
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

    @Test
    void update_WhenPostExists_ShouldUpdateAndReturnPost() {
        // Given
        Integer postId = 1;
        User user = new User(1, "Test User", "test@example.com", "testuser", null);
        Post existingPost = new Post(1, user, "Original Title", "Original Body");
        Post updateData = new Post(null, user, "Updated Title", "Updated Body");

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            return savedPost;
        });

        // When
        Post result = postService.update(postId, updateData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUser().getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getBody()).isEqualTo("Updated Body");

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void update_WhenPostDoesNotExist_ShouldThrowPostNotFound() {
        // Given
        Integer postId = 999;
        Post updateData = new Post(null, testUser, "Updated Title", "Updated Body");
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.update(postId, updateData))
                .isInstanceOf(PostNotFound.class).hasMessage("Post not found with id: " + postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void update_ShouldPreserveOriginalIdAndUserId() {
        // Given
        Integer postId = 1;
        User user = new User(5, "Test User", "test@example.com", "testuser", null);
        Post existingPost = new Post(1, user, "Original Title", "Original Body");
        Post updateData = new Post(999, user, "Updated Title", "Updated Body"); // Different ID and

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            return savedPost;
        });

        // When
        Post result = postService.update(postId, updateData);

        // Then
        assertThat(result.getId()).isEqualTo(1); // Should preserve original ID
        assertThat(result.getUser().getId()).isEqualTo(5); // Should preserve original userId
        assertThat(result.getTitle()).isEqualTo("Updated Title"); // Should update title
        assertThat(result.getBody()).isEqualTo("Updated Body"); // Should update body

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void save_WhenUserIdProvidedButUserNotFound_ShouldThrowUserNotFound() {
        // Given
        Integer userId = 999;
        Post newPost = new Post(null, null, "New Post", "New Body");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.save(newPost, userId)).isInstanceOf(UserNotFound.class)
                .hasMessage("User not found with id: " + userId);

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void update_WhenUserIdProvidedButUserNotFound_ShouldThrowUserNotFound() {
        // Given
        Integer postId = 1;
        Integer userId = 999;
        Post existingPost = new Post(1, testUser, "Original Title", "Original Body");
        Post updateData = new Post(null, null, "Updated Title", "Updated Body");

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.update(postId, updateData, userId))
                .isInstanceOf(UserNotFound.class).hasMessage("User not found with id: " + userId);

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void save_WhenUserIdProvidedAndUserExists_ShouldSetUserAndSave() {
        // Given
        Integer userId = 1;
        Post newPost = new Post(null, null, "New Post", "New Body");
        Post savedPost = new Post(3, testUser, "New Post", "New Body");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // When
        Post result = postService.save(newPost, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getUser()).isEqualTo(testUser);

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void update_WhenUserIdProvidedAndUserExists_ShouldUpdateUserAndSave() {
        // Given
        Integer postId = 1;
        Integer userId = 2;
        User newUser = new User(2, "New User", "new@example.com", "newuser", null);
        Post existingPost = new Post(1, testUser, "Original Title", "Original Body");
        Post updateData = new Post(null, null, "Updated Title", "Updated Body");

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(userRepository.findById(userId)).thenReturn(Optional.of(newUser));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            return savedPost;
        });

        // When
        Post result = postService.update(postId, updateData, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUser()).isEqualTo(newUser);
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getBody()).isEqualTo("Updated Body");

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).save(any(Post.class));
    }
}
