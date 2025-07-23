package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindAllPostsTest {

  @Mock
  private PostRepository postRepository;

  @InjectMocks
  private FindAllPosts findAllPosts;

  private Post testPost1;
  private Post testPost2;
  private List<Post> testPosts;
  private User testUser;
  private UUID testUuid1;
  private UUID testUuid2;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    testUuid1 = UUID.randomUUID();
    testUuid2 = UUID.randomUUID();
    testPost1 = new Post(1, testUuid1, testUser, "Test Post 1", "Test Body 1");
    testPost2 = new Post(2, testUuid2, testUser, "Test Post 2", "Test Body 2");
    testPosts = Arrays.asList(testPost1, testPost2);
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void findAll_ShouldReturnAllPosts() {
    // Given
    Page<Post> expectedPage = new PageImpl<>(testPosts, pageable, testPosts.size());
    when(postRepository.findAll(pageable)).thenReturn(expectedPage);

    // When
    Page<Post> result = findAllPosts.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).containsExactly(testPost1, testPost2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(1);
    verify(postRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenNoPosts_ShouldReturnEmptyPage() {
    // Given
    Page<Post> expectedPage = new PageImpl<>(List.of(), pageable, 0);
    when(postRepository.findAll(pageable)).thenReturn(expectedPage);

    // When
    Page<Post> result = findAllPosts.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isEqualTo(0);
    assertThat(result.getTotalPages()).isEqualTo(0);
    verify(postRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WithPagination_ShouldReturnCorrectPage() {
    // Given
    Pageable secondPage = PageRequest.of(1, 1);
    Page<Post> expectedPage = new PageImpl<>(List.of(testPost2), secondPage, 2);
    when(postRepository.findAll(secondPage)).thenReturn(expectedPage);

    // When
    Page<Post> result = findAllPosts.findAll(secondPage);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent()).containsExactly(testPost2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(2);
    assertThat(result.getNumber()).isEqualTo(1);
    verify(postRepository, times(1)).findAll(secondPage);
  }
}
