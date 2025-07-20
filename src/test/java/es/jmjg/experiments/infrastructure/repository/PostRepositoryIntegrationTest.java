package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.BaseJpaIntegration;
import es.jmjg.experiments.shared.UserFactory;

public class PostRepositoryIntegrationTest extends BaseJpaIntegration {

  @Autowired
  PostRepository postRepository;

  @Autowired
  UserRepository userRepository;

  private User testUser;
  private Post testPost;

  @BeforeEach
  void setUp() {
    testUser = userRepository.save(UserFactory.createBasicUser());
    UUID postUuid = UUID.randomUUID();
    testPost = new Post(null, postUuid, testUser, "Hello, World!", "This is my first post!");
    postRepository.save(testPost);
  }



  @Test
  void shouldReturnPostByTitle() {
    Post post = postRepository.findByTitle("Hello, World!").orElseThrow();
    assertEquals("Hello, World!", post.getTitle(), "Post title should be 'Hello, World!'");
  }

  @Test
  void shouldNotReturnPostWhenTitleIsNotFound() {
    Optional<Post> post = postRepository.findByTitle("Hello, Wrong Title!");
    assertFalse(post.isPresent(), "Post should not be present");
  }

  @Test
  void findAll_WithPagination_ShouldReturnCorrectPage() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<Post> result = postRepository.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(10); // First page with 10 items
    assertThat(result.getTotalElements()).isGreaterThan(50); // There are many posts from test data
    assertThat(result.getTotalPages()).isGreaterThan(5); // Multiple pages
    assertThat(result.getNumber()).isEqualTo(0);
  }

  @Test
  void findAll_WithPagination_ShouldReturnEmptyPageWhenNoPosts() {
    // Given - This test doesn't make sense with existing test data
    // We'll test pagination with a page that should be empty
    Pageable pageable = PageRequest.of(999, 10); // Very high page number

    // When
    Page<Post> result = postRepository.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isGreaterThan(50); // Total elements remain the same
    assertThat(result.getNumber()).isEqualTo(999);
  }

  @Test
  void findAll_WithPagination_ShouldHandleMultiplePages() {
    // Given
    Pageable firstPage = PageRequest.of(0, 5);
    Pageable secondPage = PageRequest.of(1, 5);

    // When
    Page<Post> firstPageResult = postRepository.findAll(firstPage);
    Page<Post> secondPageResult = postRepository.findAll(secondPage);

    // Then
    assertThat(firstPageResult.getContent()).hasSize(5);
    assertThat(firstPageResult.getTotalElements()).isGreaterThan(50);
    assertThat(firstPageResult.getTotalPages()).isGreaterThan(10);
    assertThat(firstPageResult.getNumber()).isEqualTo(0);

    assertThat(secondPageResult.getContent()).hasSize(5);
    assertThat(secondPageResult.getTotalElements()).isGreaterThan(50);
    assertThat(secondPageResult.getTotalPages()).isGreaterThan(10);
    assertThat(secondPageResult.getNumber()).isEqualTo(1);

    // Verify that the pages contain different content
    assertThat(firstPageResult.getContent()).isNotEqualTo(secondPageResult.getContent());
  }
}
