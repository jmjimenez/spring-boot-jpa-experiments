package es.jmjg.experiments.infrastructure.controller.tag.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TagControllerGetUsersByTagUuidIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldFindUsersByTag() {
    // Given
    UUID tagUuid = TestDataSamples.TAG_TECHNOLOGY_UUID;

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
      "/api/tags/" + tagUuid + "/users",
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
  void shouldReturnNotFoundWhenFindingUsersByNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
      "/api/tags/" + nonExistentUuid + "/users",
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void whenUnauthorized_shouldFindUsersByTag() {
    // Given
    UUID tagUuid = TestDataSamples.TAG_TECHNOLOGY_UUID;

    // When
    ResponseEntity<Object> response = restTemplate.exchange(
      "/api/tags/" + tagUuid + "/users",
      HttpMethod.GET,
      null,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
