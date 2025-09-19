package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.infrastructure.controller.post.mapper.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.shared.BaseIntegration;

class FindAllPostsIntegrationTest extends BaseIntegration {

  @Autowired
  private FindAllPosts findAllPosts;

  private Pageable pageable;
  @Autowired
  private PostMapper postMapper;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void findAll_ShouldReturnAllPosts() {
    // When
    Page<Post> result = findAllPosts.findAll(postMapper.toFindAllPostsDto(pageable));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(10); // First page with 10 items
    assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(50); // At least 50 from Flyway + 2 from test
    assertThat(result.getTotalPages()).isGreaterThanOrEqualTo(5); // At least 5 pages with 10 items per page
  }

  @Test
  void findAll_WhenNoPosts_ShouldReturnEmptyPage() {
    // Given - Test with a page that should be empty
    Pageable emptyPage = PageRequest.of(999, 10);

    // When
    Page<Post> result = findAllPosts.findAll(postMapper.toFindAllPostsDto(emptyPage));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(50); // At least 50 posts from Flyway
    assertThat(result.getNumber()).isEqualTo(999);
  }

  @Test
  void findAll_WithPagination_ShouldReturnCorrectPage() {
    Pageable firstPage = PageRequest.of(0, 5);

    // When
    Page<Post> result = findAllPosts.findAll(postMapper.toFindAllPostsDto(firstPage));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(5); // First page with 5 items
    assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(50); // At least 50 from Flyway + 2 from test
    assertThat(result.getTotalPages()).isGreaterThanOrEqualTo(10); // At least 10 pages with 5 items per page
    assertThat(result.getNumber()).isEqualTo(0);
  }
}
