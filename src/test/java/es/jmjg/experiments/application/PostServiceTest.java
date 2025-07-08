package es.jmjg.experiments.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Mock
    private UpdatePost updatePost;

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
    void save_ShouldSaveAndReturnPost() {
        // Given
        UUID newUuid = UUID.randomUUID();
        Post newPost = new Post(null, newUuid, testUser, "New Post", "New Body");
        Post savedPost = new Post(3, newUuid, testUser, "New Post", "New Body");

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
        UUID newUuid = UUID.randomUUID();
        Post newPost = new Post(null, newUuid, testUser, "New Post", "New Body");
        Post savedPost = new Post(3, newUuid, testUser, "New Post", "New Body");

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
        UUID newUuid = UUID.randomUUID();
        Post newPost = new Post(null, newUuid, null, "New Post", "New Body");
        Post savedPost = new Post(3, newUuid, null, "New Post", "New Body");

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
        UUID updateUuid = UUID.randomUUID();
        Post updateData = new Post(null, updateUuid, user, "Updated Title", "Updated Body");
        Post expectedResult = new Post(1, updateUuid, user, "Updated Title", "Updated Body");

        when(updatePost.update(postId, updateData)).thenReturn(expectedResult);

        // When
        Post result = postService.update(postId, updateData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUser().getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getBody()).isEqualTo("Updated Body");

        verify(updatePost, times(1)).update(postId, updateData);
    }

    @Test
    void update_WhenPostDoesNotExist_ShouldThrowPostNotFound() {
        // Given
        Integer postId = 999;
        UUID updateUuid = UUID.randomUUID();
        Post updateData = new Post(null, updateUuid, testUser, "Updated Title", "Updated Body");
        when(updatePost.update(postId, updateData)).thenThrow(new PostNotFound(postId));

        // When & Then
        assertThatThrownBy(() -> postService.update(postId, updateData))
                .isInstanceOf(PostNotFound.class).hasMessage("Post not found with id: " + postId);

        verify(updatePost, times(1)).update(postId, updateData);
    }

    @Test
    void update_ShouldPreserveOriginalIdAndUserId() {
        // Given
        Integer postId = 1;
        User user = new User(5, "Test User", "test@example.com", "testuser", null);
        UUID updateUuid = UUID.randomUUID();
        Post updateData = new Post(999, updateUuid, user, "Updated Title", "Updated Body"); // Different
                                                                                            // ID
        Post expectedResult = new Post(1, updateUuid, user, "Updated Title", "Updated Body");

        when(updatePost.update(postId, updateData)).thenReturn(expectedResult);

        // When
        Post result = postService.update(postId, updateData);

        // Then
        assertThat(result.getId()).isEqualTo(1); // Should preserve original ID
        assertThat(result.getUser().getId()).isEqualTo(5); // Should preserve original userId
        assertThat(result.getTitle()).isEqualTo("Updated Title"); // Should update title
        assertThat(result.getBody()).isEqualTo("Updated Body"); // Should update body

        verify(updatePost, times(1)).update(postId, updateData);
    }

    @Test
    void save_WhenUserIdProvidedButUserNotFound_ShouldThrowUserNotFound() {
        // Given
        Integer userId = 999;
        UUID newUuid = UUID.randomUUID();
        Post newPost = new Post(null, newUuid, null, "New Post", "New Body");
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
        UUID updateUuid = UUID.randomUUID();
        Post updateData = new Post(null, updateUuid, null, "Updated Title", "Updated Body");

        when(updatePost.update(postId, updateData, userId)).thenThrow(new UserNotFound(userId));

        // When & Then
        assertThatThrownBy(() -> postService.update(postId, updateData, userId))
                .isInstanceOf(UserNotFound.class).hasMessage("User not found with id: " + userId);

        verify(updatePost, times(1)).update(postId, updateData, userId);
    }

    @Test
    void save_WhenUserIdProvidedAndUserExists_ShouldSetUserAndSave() {
        // Given
        Integer userId = 1;
        UUID newUuid = UUID.randomUUID();
        Post newPost = new Post(null, newUuid, null, "New Post", "New Body");
        Post savedPost = new Post(3, newUuid, testUser, "New Post", "New Body");

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
        UUID updateUuid = UUID.randomUUID();
        Post updateData = new Post(null, updateUuid, null, "Updated Title", "Updated Body");
        Post expectedResult = new Post(1, updateUuid, newUser, "Updated Title", "Updated Body");

        when(updatePost.update(postId, updateData, userId)).thenReturn(expectedResult);

        // When
        Post result = postService.update(postId, updateData, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUser()).isEqualTo(newUser);
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getBody()).isEqualTo("Updated Body");

        verify(updatePost, times(1)).update(postId, updateData, userId);
    }
}
