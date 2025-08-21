package es.jmjg.experiments.application.post;

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

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.shared.PostFactory;

@ExtendWith(MockitoExtension.class)
class SavePostTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private SavePost savePost;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User(1, UUID.randomUUID(), "Test User", "test@example.com", "testuser", "encodedPassword123", null);
  }

  @Test
  void save_ShouldSaveAndReturnPost() {
    // Given
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, "New Post", "New Body");
    Post savedPost = new Post(3, savePostDto.uuid(), testUser, "New Post", "New Body");

    when(userRepository.findByUuid(savePostDto.userUuid())).thenReturn(Optional.of(testUser));
    when(postRepository.save(any(Post.class))).thenReturn(savedPost);

    // When
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getTitle()).isEqualTo("New Post");
    assertThat(result.getBody()).isEqualTo("New Body");
    verify(userRepository, times(1)).findByUuid(savePostDto.userUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void save_WhenPostAlreadyHasUser_ShouldNotFetchUser() {
    // Given
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, "New Post", "New Body");
    Post savedPost = new Post(3, savePostDto.uuid(), testUser, "New Post", "New Body");

    when(userRepository.findByUuid(savePostDto.userUuid())).thenReturn(Optional.of(testUser));
    when(postRepository.save(any(Post.class))).thenReturn(savedPost);

    // When
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    verify(userRepository, times(1)).findByUuid(savePostDto.userUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void save_WhenUserIdProvidedAndUserExists_ShouldSetUserAndSave() {
    // Given
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, "New Post", "New Body");
    Post savedPost = new Post(3, savePostDto.uuid(), testUser, "New Post", "New Body");

    when(userRepository.findByUuid(savePostDto.userUuid())).thenReturn(Optional.of(testUser));
    when(postRepository.save(any(Post.class))).thenReturn(savedPost);

    // When
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getTitle()).isEqualTo("New Post");
    assertThat(result.getBody()).isEqualTo("New Body");
    assertThat(result.getUser()).isEqualTo(testUser);

    verify(userRepository, times(1)).findByUuid(savePostDto.userUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }
}
