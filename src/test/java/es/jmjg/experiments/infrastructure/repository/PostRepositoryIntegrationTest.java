package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.shared.BaseJpaIntegration;

public class PostRepositoryIntegrationTest extends BaseJpaIntegration {

  @Autowired
  PostRepository postRepository;

  // Sample posts from Flyway migration data
  private static final String LEANNE_POST_TITLE = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";
  private static final String LEANNE_POST_UUID = "550e8400-e29b-41d4-a716-446655440006";

  private static final String ERVIN_POST_TITLE = "et ea vero quia laudantium autem";
  private static final String ERVIN_POST_UUID = "550e8400-e29b-41d4-a716-446655440016";

  private static final String CLEMENTINE_POST_TITLE = "asperiores ea ipsam voluptatibus modi minima quia sint";
  private static final String CLEMENTINE_POST_UUID = "550e8400-e29b-41d4-a716-446655440026";

  @Test
  void shouldReturnPostByTitle() {
    Post post = postRepository.findByTitle(LEANNE_POST_TITLE).orElseThrow();
    assertEquals(LEANNE_POST_TITLE, post.getTitle(), "Post title should match the migration data");
    assertEquals(UUID.fromString(LEANNE_POST_UUID), post.getUuid(), "Post UUID should match the migration data");
  }

  @Test
  void shouldNotReturnPostWhenTitleIsNotFound() {
    Optional<Post> post = postRepository.findByTitle("Hello, Wrong Title!");
    assertFalse(post.isPresent(), "Post should not be present");
  }

  @Test
  void shouldFindMultiplePostsByTitle() {
    // Test finding posts from different users
    Optional<Post> leannePost = postRepository.findByTitle(LEANNE_POST_TITLE);
    Optional<Post> ervinPost = postRepository.findByTitle(ERVIN_POST_TITLE);
    Optional<Post> clementinePost = postRepository.findByTitle(CLEMENTINE_POST_TITLE);

    assertTrue(leannePost.isPresent(), "Leanne's post should be found");
    assertTrue(ervinPost.isPresent(), "Ervin's post should be found");
    assertTrue(clementinePost.isPresent(), "Clementine's post should be found");

    assertEquals(UUID.fromString(LEANNE_POST_UUID), leannePost.get().getUuid());
    assertEquals(UUID.fromString(ERVIN_POST_UUID), ervinPost.get().getUuid());
    assertEquals(UUID.fromString(CLEMENTINE_POST_UUID), clementinePost.get().getUuid());
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
    assertThat(result.getTotalElements()).isEqualTo(50); // Exactly 50 posts from migration
    assertThat(result.getTotalPages()).isEqualTo(5); // 5 pages with 10 items per page
    assertThat(result.getNumber()).isEqualTo(0);
  }

  @Test
  void findAll_WithPagination_ShouldReturnEmptyPageWhenNoPosts() {
    // Given - Test with a page that should be empty
    Pageable pageable = PageRequest.of(999, 10); // Very high page number

    // When
    Page<Post> result = postRepository.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isEqualTo(50); // Exactly 50 posts from migration
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
    assertThat(firstPageResult.getTotalElements()).isEqualTo(50);
    assertThat(firstPageResult.getTotalPages()).isEqualTo(10); // 10 pages with 5 items per page
    assertThat(firstPageResult.getNumber()).isEqualTo(0);

    assertThat(secondPageResult.getContent()).hasSize(5);
    assertThat(secondPageResult.getTotalElements()).isEqualTo(50);
    assertThat(secondPageResult.getTotalPages()).isEqualTo(10);
    assertThat(secondPageResult.getNumber()).isEqualTo(1);

    // Verify that the pages contain different content
    assertThat(firstPageResult.getContent()).isNotEqualTo(secondPageResult.getContent());
  }

  @Test
  void shouldFindAllPostsWithCorrectTotalCount() {
    // Given
    Pageable allPostsPage = PageRequest.of(0, 50);

    // When
    Page<Post> result = postRepository.findAll(allPostsPage);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(50); // All 50 posts from migration
    assertThat(result.getTotalElements()).isEqualTo(50);
    assertThat(result.getTotalPages()).isEqualTo(1); // Single page with all posts
    assertThat(result.getNumber()).isEqualTo(0);
  }
}
