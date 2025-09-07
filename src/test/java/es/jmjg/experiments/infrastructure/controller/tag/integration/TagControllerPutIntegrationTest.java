package es.jmjg.experiments.infrastructure.controller.tag.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagRequestDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TagControllerPutIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldUpdateExistingTagName() {
    // Given
    UUID tagUuid = TestDataSamples.JAVA_UUID;
    UpdateTagRequestDto updateDto = new UpdateTagRequestDto(tagUuid, "updated-java");

    // When
    HttpEntity<UpdateTagRequestDto> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD, updateDto);
    ResponseEntity<UpdateTagResponseDto> response = restTemplate.exchange(
      "/api/tags/" + tagUuid, HttpMethod.PUT, request,
      UpdateTagResponseDto.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UpdateTagResponseDto updatedTag = response.getBody();
    assertThat(updatedTag).isNotNull().satisfies(t -> {
      assertThat(t.getUuid()).isEqualTo(tagUuid);
      assertThat(t.getName()).isEqualTo("updated-java");
    });
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    UpdateTagRequestDto updateDto = new UpdateTagRequestDto(nonExistentUuid, "updated-tag");

    // When
    HttpEntity<UpdateTagRequestDto> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
      TestDataSamples.ADMIN_PASSWORD, updateDto);
    ResponseEntity<UpdateTagResponseDto> response = restTemplate.exchange(
      "/api/tags/" + nonExistentUuid, HttpMethod.PUT, request,
      UpdateTagResponseDto.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
