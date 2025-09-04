package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.post.dto.SavePostDto;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class SavePostTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ProcessPostTags processPostTags;

  @InjectMocks
  private SavePost savePost;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
  }

  @Test
  void save_ShouldSaveAndReturnPost() {
    // Given
    Post newPost = PostFactory.createBasicPost(testUser);

    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    when(postRepository.save(any(Post.class))).thenReturn(newPost);
    doNothing().when(processPostTags).processTagsForPost(any(Post.class), anyList());

    // When
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, newPost.getTitle(), newPost.getBody());
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(newPost.getId());
    assertThat(result.getTitle()).isEqualTo(newPost.getTitle());
    assertThat(result.getBody()).isEqualTo(newPost.getBody());
    verify(userRepository, times(1)).findByUuid(testUser.getUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void save_WhenPostAlreadyHasUser_ShouldNotFetchUser() {
    // Given
    Post newPost = PostFactory.createBasicPost(testUser);

    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    when(postRepository.save(any(Post.class))).thenReturn(newPost);
    doNothing().when(processPostTags).processTagsForPost(any(Post.class), anyList());

    // When
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, newPost.getTitle(), newPost.getBody());
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(newPost.getId());
    verify(userRepository, times(1)).findByUuid(testUser.getUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void save_WhenUserIdProvidedAndUserExists_ShouldSetUserAndSave() {
    // Given
    Post newPost = PostFactory.createBasicPost(testUser);

    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    when(postRepository.save(any(Post.class))).thenReturn(newPost);
    doNothing().when(processPostTags).processTagsForPost(any(Post.class), anyList());

    // When
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, newPost.getTitle(), newPost.getBody());
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(newPost.getId());
    assertThat(result.getTitle()).isEqualTo(newPost.getTitle());
    assertThat(result.getBody()).isEqualTo(newPost.getBody());
    assertThat(result.getUser()).isEqualTo(testUser);

    verify(userRepository, times(1)).findByUuid(testUser.getUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }
}
