package es.jmjg.experiments.infrastructure.controller.tag.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TagControllerGetPostsByTagNameIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldFindPostsByTagName() {
    // Given
    String tagName = "technology";

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
      "/api/tags/search/posts?name=" + tagName,
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Object responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldReturnNotFoundWhenFindingPostsByNonExistentTagName() {
    // Given
    String nonExistentTagName = "nonexistent-tag";

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
      "/api/tags/search/posts?name=" + nonExistentTagName,
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void whenUnauthorized_shouldFindPostsByTagName() {
    // Given
    String tagName = "technology";

    // When
    ResponseEntity<Object> response = restTemplate.exchange(
      "/api/tags/search/posts?name=" + tagName,
      HttpMethod.GET,
      null,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
