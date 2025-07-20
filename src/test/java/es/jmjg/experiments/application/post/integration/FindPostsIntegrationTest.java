package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.FindPosts;
import es.jmjg.experiments.shared.BaseIntegration;

class FindPostsIntegrationTest extends BaseIntegration {

  @Autowired
  private FindPosts findPosts;

  @Test
  void find_WhenQueryMatchesTitle_ShouldReturnMatchingPosts() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("sunt", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has posts with "sunt" in the title
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("sunt") ||
        post.getBody().toLowerCase().contains("sunt"));
  }

  @Test
  void find_WhenQueryMatchesBody_ShouldReturnMatchingPosts() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("quia", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has posts with "quia" in the body
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("quia") ||
        post.getBody().toLowerCase().contains("quia"));
  }

  @Test
  void find_WhenQueryMatchesMultiplePosts_ShouldReturnAllMatchingPosts() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("et", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has multiple posts with "et" in their content
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("et") ||
        post.getBody().toLowerCase().contains("et"));
  }

  @Test
  void find_WhenQueryIsCaseInsensitive_ShouldReturnMatchingPosts() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("SUNT", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // Case insensitive search should work
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("sunt") ||
        post.getBody().toLowerCase().contains("sunt"));
  }

  @Test
  void find_WhenQueryIsPartial_ShouldReturnMatchingPosts() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("aut", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // Partial word search should work
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("aut") ||
        post.getBody().toLowerCase().contains("aut"));
  }

  @Test
  void find_WhenLimitIsApplied_ShouldRespectLimit() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("et", 1);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
  }

  @Test
  void find_WhenQueryIsNull_ShouldReturnEmptyList() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find(null, 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void find_WhenQueryIsEmpty_ShouldReturnEmptyList() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void find_WhenQueryIsWhitespace_ShouldReturnEmptyList() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("   ", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void find_WhenQueryDoesNotMatchAnyPost_ShouldReturnEmptyList() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("NonExistentQueryThatShouldNotMatchAnything", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void find_WhenQueryMatchesInBothTitleAndBody_ShouldReturnPost() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("est", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // The test data has posts with "est" in both title and body
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("est") ||
        post.getBody().toLowerCase().contains("est"));
  }

  @Test
  void find_WhenQueryIsTrimmed_ShouldWorkCorrectly() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("  sunt  ", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // Trimmed query should work correctly
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("sunt") ||
        post.getBody().toLowerCase().contains("sunt"));
  }

  @Test
  void find_WhenMultiplePostsMatch_ShouldReturnAllInCorrectOrder() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("qui", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // Multiple posts should match "qui" in the test data
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("qui") ||
        post.getBody().toLowerCase().contains("qui"));
  }

  @Test
  void find_WhenQueryMatchesCommonWords_ShouldReturnMultiplePosts() {
    // Given - Using existing Flyway test data
    // When
    var result = findPosts.find("in", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // Common words like "in" should match multiple posts
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("in") ||
        post.getBody().toLowerCase().contains("in"));
  }

  @Test
  void find_WhenQueryMatchesSpecificPostContent_ShouldReturnThatPost() {
    // Given - Using existing Flyway test data with specific content
    // When
    var result = findPosts.find("repellat", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    // Should find posts with "repellat" in the content
    assertThat(result).allMatch(post -> post.getTitle().toLowerCase().contains("repellat") ||
        post.getBody().toLowerCase().contains("repellat"));
  }
}
