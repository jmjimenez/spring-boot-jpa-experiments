package es.jmjg.experiments.infrastructure.controller.tag.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagRequestDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TagControllerPostIntegrationTest extends BaseControllerIntegration {
  @Test
  void shouldCreateNewTagWhenTagIsValid() {
    // Given
    SaveTagRequestDto tagDto = new SaveTagRequestDto(UUID.randomUUID(), "new-tag");

    // When
    HttpEntity<SaveTagRequestDto> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD, tagDto);
    ResponseEntity<SaveTagResponseDto> response = restTemplate.exchange(
      "/api/tags", HttpMethod.POST, request, SaveTagResponseDto.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    // Verify Location header is present and correct
    String locationHeader = response.getHeaders().getFirst("Location");
    assertThat(locationHeader).isNotNull();
    assertThat(locationHeader).startsWith("/api/tags/");

    SaveTagResponseDto tag = response.getBody();
    assertThat(tag).isNotNull().satisfies(t -> {
      assertThat(t.getName()).isEqualTo("new-tag");
      assertThat(t.getUuid()).isNotNull();
      // Verify the Location header contains the correct UUID
      assertThat(locationHeader).isEqualTo("/api/tags/" + t.getUuid());
    });
  }

  @Test
  void shouldReturnConflictWhenCreatingTagWithDuplicateName() {
    // Given
    SaveTagRequestDto tagDto1 = new SaveTagRequestDto(UUID.randomUUID(), "duplicate-tag");
    SaveTagRequestDto tagDto2 = new SaveTagRequestDto(UUID.randomUUID(), "duplicate-tag");

    // Create first tag
    HttpEntity<SaveTagRequestDto> request1 = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD, tagDto1);
    restTemplate.exchange("/api/tags", HttpMethod.POST, request1, SaveTagResponseDto.class);

    // When - Try to create second tag with same name
    HttpEntity<SaveTagRequestDto> request2 = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD, tagDto2);
    ResponseEntity<Object> response = restTemplate.exchange(
      "/api/tags", HttpMethod.POST, request2, Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldReturnConflictWhenCreatingTagWithExistingNameFromMigration() {
    // Given - Try to create a tag with a name that exists in migration data
    SaveTagRequestDto tagDto = new SaveTagRequestDto(UUID.randomUUID(), "technology");

    // When
    HttpEntity<SaveTagRequestDto> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD, tagDto);
    ResponseEntity<Object> response = restTemplate.exchange(
      "/api/tags", HttpMethod.POST, request, Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldReturnConflictWhenCreatingTagWithDuplicateUuid() {
    // Given
    UUID duplicateUuid = UUID.randomUUID();
    SaveTagRequestDto tagDto1 = new SaveTagRequestDto(duplicateUuid, "first-tag");
    SaveTagRequestDto tagDto2 = new SaveTagRequestDto(duplicateUuid, "second-tag");

    // Create first tag
    HttpEntity<SaveTagRequestDto> request1 = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD, tagDto1);
    restTemplate.exchange("/api/tags", HttpMethod.POST, request1, SaveTagResponseDto.class);

    // When - Try to create second tag with same UUID
    HttpEntity<SaveTagRequestDto> request2 = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD, tagDto2);
    ResponseEntity<Object> response = restTemplate.exchange(
      "/api/tags", HttpMethod.POST, request2, Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }
  @Test
  void whenUnauthorized_shouldReturnUnauthorized() {
    // Given
    SaveTagRequestDto tagDto = new SaveTagRequestDto(UUID.randomUUID(), "new-tag");

    // When
    HttpEntity<SaveTagRequestDto> request = createUnauthenticatedRequest(tagDto);
    ResponseEntity<SaveTagResponseDto> response = restTemplate.exchange(
      "/api/tags", HttpMethod.POST, request, SaveTagResponseDto.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
