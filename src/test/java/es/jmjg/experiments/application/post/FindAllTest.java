package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.repository.PostRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class FindAllTest {

  @Mock
  private PostRepository postRepository;

  @InjectMocks
  private FindPosts findPosts;

  @BeforeEach
  void setUp() {
  }

  @Test
  void find_ShouldReturnPosts_WhenQueryIsValid() {
    // Given
    var postOwner = UserFactory.createBasicUser();
    var testPost1 = PostFactory.createBasicPost(postOwner);
    var testPost2 = PostFactory.createBasicPost(postOwner);
    List<Post> testPosts = Arrays.asList(testPost1, testPost2);
    String query = "test";
    int limit = 10;
    Pageable pageable = PageRequest.of(0, limit);
    when(postRepository.searchByContent(query, pageable)).thenReturn(testPosts);
    // When
    List<Post> result = findPosts.find(query, limit);
    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(testPosts);
    verify(postRepository, times(1)).searchByContent(query, pageable);
  }

  @Test
  void find_ShouldReturnEmptyList_WhenQueryIsNull() {
    // When
    List<Post> result = findPosts.find(null, 10);
    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
    verify(postRepository, times(0)).searchByContent(anyString(), any(Pageable.class));
  }

  @Test
  void find_ShouldReturnEmptyList_WhenQueryIsEmpty() {
    // When
    List<Post> result = findPosts.find("   ", 10);
    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
    verify(postRepository, times(0)).searchByContent(anyString(), any(Pageable.class));
  }
}
