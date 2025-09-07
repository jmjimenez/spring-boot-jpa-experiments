package es.jmjg.experiments.infrastructure.controller.tag.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByPatternResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TagControllerGetSearchIntegrationTest extends BaseControllerIntegration {
  @SuppressWarnings("null")
  @Test
  void shouldFindTagsByPattern() {
    // Given
    String pattern = "tech";

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindTagByPatternResponseDto[]> response = restTemplate.exchange(
      "/api/tags/search?pattern=" + pattern,
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    FindTagByPatternResponseDto[] tags = response.getBody();
    assertThat(tags).isNotNull();
    assertThat(tags).isNotEmpty();

    for (FindTagByPatternResponseDto tag : tags) {
      assertThat(tag.getPosts()).isNotNull();
      assertThat(tag.getUsers()).isNotNull();
      // Technology tag should have posts and users based on migration data
      assertThat(tag.getPosts()).isNotEmpty();
      assertThat(tag.getUsers()).isNotEmpty();
    }
  }

  @Test
  void shouldFindTagsByPatternWithNoResults() {
    // Given
    String pattern = "nonexistent";

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindTagByPatternResponseDto[]> response = restTemplate.exchange(
      "/api/tags/search?pattern=" + pattern,
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    FindTagByPatternResponseDto[] tags = response.getBody();
    assertThat(tags).isNotNull();
    assertThat(tags).isEmpty();
  }

  @SuppressWarnings("null")
  @Test
  void shouldFindTagsByPatternWithMultipleTags() {
    // Given
    String pattern = "tech";

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindTagByPatternResponseDto[]> response = restTemplate.exchange(
      "/api/tags/search?pattern=" + pattern,
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    FindTagByPatternResponseDto[] tags = response.getBody();
    assertThat(tags).isNotNull();
    assertThat(tags).isNotEmpty();

    // Verify that each tag has posts and users properties
    for (FindTagByPatternResponseDto tag : tags) {
      assertThat(tag.getPosts()).isNotNull();
      assertThat(tag.getUsers()).isNotNull();
      // All tags should have posts and users based on migration data
      assertThat(tag.getPosts()).isNotEmpty();
      assertThat(tag.getUsers()).isNotEmpty();
    }
  }
}
