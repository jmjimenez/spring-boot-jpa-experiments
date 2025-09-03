package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.*;
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
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.infrastructure.controller.exception.PostNotFoundException;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindPostByUuidTest {

  @Mock
  private PostRepository postRepository;

  @InjectMocks
  private FindPostByUuid findPostByUuid;

  private Post testPost;

  @BeforeEach
  void setUp() {
    var testUser = UserFactory.createBasicUser();
    testPost = PostFactory.createBasicPost(testUser);
  }

  @Test
  void findByUuid_WhenPostExists_ShouldReturnPost() {
    // Given
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.of(testPost));

    // When
    Post result = findPostByUuid.findByUuid(testPost.getUuid());

    // Then
    assertThat(result).isEqualTo(testPost);
    verify(postRepository, times(1)).findByUuid(testPost.getUuid());
  }

  @Test
  void findByUuid_WhenPostDoesNotExist_ShouldThrowPostNotFoundException() {
    // Given
    var nonExistentUuid = UUID.randomUUID();
    when(postRepository.findByUuid(nonExistentUuid)).thenReturn(Optional.empty());

    // When
    assertThatThrownBy(() -> findPostByUuid.findByUuid(nonExistentUuid))
        .isInstanceOf(PostNotFoundException.class)
        .hasMessage("Post not found");

    // Then
    verify(postRepository, times(1)).findByUuid(nonExistentUuid);
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldThrowIllegalArgumentException() {
    // When
    assertThatThrownBy(() -> findPostByUuid.findByUuid(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UUID cannot be null");

    // Then
    verify(postRepository, never()).findByUuid(null);
  }
}
