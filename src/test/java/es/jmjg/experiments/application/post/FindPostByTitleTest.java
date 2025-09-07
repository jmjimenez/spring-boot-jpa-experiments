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

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.repository.PostRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindPostByTitleTest {

  @Mock
  private PostRepository postRepository;

  @InjectMocks
  private FindPostByTitle findPostByTitle;

  private Post testPost;

  @BeforeEach
  void setUp() {
    var testUser = UserFactory.createBasicUser();
    testPost = PostFactory.createBasicPost(testUser);
  }

  @Test
  void findByTitle_WhenPostExists_ShouldReturnPost() {
    // Given
    String title = "Test Post";
    when(postRepository.findByTitle(title)).thenReturn(Optional.of(testPost));

    // When
    Optional<Post> result = findPostByTitle.findByTitle(title);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testPost);
    verify(postRepository, times(1)).findByTitle(title);
  }

  @Test
  void findByTitle_WhenPostDoesNotExist_ShouldReturnEmpty() {
    // Given
    String title = "Non-existent Post";
    when(postRepository.findByTitle(title)).thenReturn(Optional.empty());

    // When
    Optional<Post> result = findPostByTitle.findByTitle(title);

    // Then
    assertThat(result).isEmpty();
    verify(postRepository, times(1)).findByTitle(title);
  }

  @Test
  void findByTitle_WhenTitleIsNull_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle(null);

    // Then
    assertThat(result).isEmpty();
    verify(postRepository, never()).findByTitle(any());
  }

  @Test
  void findByTitle_WhenTitleIsEmpty_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle("");

    // Then
    assertThat(result).isEmpty();
    verify(postRepository, never()).findByTitle(any());
  }

  @Test
  void findByTitle_WhenTitleIsBlank_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle("   ");

    // Then
    assertThat(result).isEmpty();
    verify(postRepository, never()).findByTitle(any());
  }

  @Test
  void findByTitle_WhenTitleHasLeadingTrailingSpaces_ShouldCallRepository() {
    // Given
    String titleWithSpaces = "  Test Post  ";
    String trimmedTitle = "Test Post";
    when(postRepository.findByTitle(trimmedTitle)).thenReturn(Optional.of(testPost));

    // When
    Optional<Post> result = findPostByTitle.findByTitle(titleWithSpaces);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testPost);
    verify(postRepository, times(1)).findByTitle(trimmedTitle);
  }
}
