package es.jmjg.experiments.infrastructure.controller.tag.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByUuidResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TagControllerGetByUuidIntegrationTest extends BaseControllerIntegration {
  @Test
  void shouldFindTagByUuid() {
    // Given
    UUID tagUuid = TestDataSamples.TAG_TECHNOLOGY_UUID;

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.exchange(
      "/api/tags/" + tagUuid,
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindTagByUuidResponseDto tag = response.getBody();
    assertThat(tag).isNotNull().satisfies(t -> {
      assertThat(t.getUuid()).isEqualTo(tagUuid);
      assertThat(t.getName()).isEqualTo(TestDataSamples.TAG_TECHNOLOGY);
      assertThat(t.getPosts()).isNotNull();
      assertThat(t.getUsers()).isNotNull();
      // Technology tag should have posts and users based on migration data
      assertThat(t.getPosts()).isNotEmpty();
      assertThat(t.getUsers()).isNotEmpty();
    });
  }

  @Test
  void shouldFindTagByUuidWithNoRelations() {
    // Given
    UUID tagUuid = TestDataSamples.TAG_NOT_USED_UUID;

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.exchange(
      "/api/tags/" + tagUuid,
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindTagByUuidResponseDto tag = response.getBody();
    assertThat(tag).isNotNull().satisfies(t -> {
      assertThat(t.getUuid()).isEqualTo(tagUuid);
      assertThat(t.getName()).isEqualTo("not-used");
      assertThat(t.getPosts()).isNotNull();
      assertThat(t.getUsers()).isNotNull();
      // Not-used tag should have no posts or users
      assertThat(t.getPosts()).isEmpty();
      assertThat(t.getUsers()).isEmpty();
    });
  }

  @Test
  void shouldReturnNotFoundWhenTagByUuidDoesNotExist() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.exchange(
      "/api/tags/" + nonExistentUuid,
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void whenRequestIsNotAuthenticated_shouldFindTagByUuid() {
    // Given
    UUID tagUuid = TestDataSamples.TAG_TECHNOLOGY_UUID;

    // When
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.exchange(
      "/api/tags/" + tagUuid,
      HttpMethod.GET,
      null,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindTagByUuidResponseDto tag = response.getBody();
    assertThat(tag).isNotNull().satisfies(t -> {
      assertThat(t.getUuid()).isEqualTo(tagUuid);
      assertThat(t.getName()).isEqualTo(TestDataSamples.TAG_TECHNOLOGY);
      assertThat(t.getPosts()).isNotNull();
      assertThat(t.getUsers()).isNotNull();
      // Technology tag should have posts and users based on migration data
      assertThat(t.getPosts()).isNotEmpty();
      assertThat(t.getUsers()).isNotEmpty();
    });
  }

  @Test
  void whenUnauthorized_shouldFindTagByUuid() {
    // Given
    UUID tagUuid = TestDataSamples.TAG_TECHNOLOGY_UUID;

    // When
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.exchange(
      "/api/tags/" + tagUuid,
      HttpMethod.GET,
      null,
      new ParameterizedTypeReference<>() { }
    );

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
