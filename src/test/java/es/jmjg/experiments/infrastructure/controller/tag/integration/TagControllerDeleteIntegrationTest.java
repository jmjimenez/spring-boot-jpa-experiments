package es.jmjg.experiments.infrastructure.controller.tag.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TagControllerDeleteIntegrationTest extends BaseControllerIntegration {
  @Test
  void shouldDeleteUnusedTagByUuid() {
    // Given
    UUID tagUuid = TestDataSamples.NOT_USED_UUID;

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tags/" + tagUuid, HttpMethod.DELETE, request, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void shouldReturnErrorWhenDeletingTagUsedByUser() {
    // Given
    UUID tagUuid = TestDataSamples.DEVELOPER_UUID;

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
      "/api/tags/" + tagUuid, HttpMethod.DELETE, request, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
      "/api/tags/" + nonExistentUuid, HttpMethod.DELETE, request, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnErrorWhenDeletingTagUsedByPost() {
    // Given
    UUID tagUuid = TestDataSamples.TECHNOLOGY_UUID;

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
      "/api/tags/" + tagUuid, HttpMethod.DELETE, request, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }
  @Test
  void when_Unauthorized_shouldReturnUnauthorized() {
    // Given
    UUID tagUuid = TestDataSamples.NOT_USED_UUID;

    // When
    ResponseEntity<Void> response = restTemplate.exchange(
      "/api/tags/" + tagUuid, HttpMethod.DELETE, null, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
