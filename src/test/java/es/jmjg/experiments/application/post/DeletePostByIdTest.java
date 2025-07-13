package es.jmjg.experiments.application.post;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.infrastructure.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class DeletePostByIdTest {

  @Mock private PostRepository postRepository;

  @InjectMocks private DeletePostById deletePostById;

  @Test
  void deleteById_ShouldCallRepositoryDelete() {
    // Given
    Integer postId = 1;
    doNothing().when(postRepository).deleteById(postId);

    // When
    deletePostById.deleteById(postId);

    // Then
    verify(postRepository, times(1)).deleteById(postId);
  }
}
