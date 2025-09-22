package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostTagsRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostTagsResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class PostControllerPatchTagsIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldPatchTagsWhenPostExists() {
    final String username = TestDataSamples.LEANNE_USERNAME;
    final String password = TestDataSamples.LEANNE_PASSWORD;
    final UUID postUuid = TestDataSamples.LEANNE_POST_UUID;
    final List<String> newTags = List.of(TestDataSamples.TAG_JAVA, TestDataSamples.TAG_TECHNOLOGY);

    UpdatePostTagsRequestDto requestDto = new UpdatePostTagsRequestDto(newTags);
    final String accessToken = createAccessToken(username, password);
    HttpEntity<UpdatePostTagsRequestDto> request = createAuthenticatedRequestWithAccessToken(accessToken, requestDto);

    ResponseEntity<UpdatePostTagsResponseDto> response = restTemplate.exchange(
        "/api/posts/" + postUuid + "/tags", HttpMethod.PATCH, request, UpdatePostTagsResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UpdatePostTagsResponseDto post = response.getBody();
    assertThat(post).isNotNull();
    assertThat(post.getTags()).extracting("name").containsExactlyInAnyOrderElementsOf(newTags);
  }

  @Test
  void shouldReturnUnauthorizedWhenUserNotAuthenticated() {
    final UUID postUuid = TestDataSamples.LEANNE_UUID; // Replace with actual post UUID for Leanne
    final List<String> newTags = List.of(TestDataSamples.TAG_JAVA, TestDataSamples.TAG_TECHNOLOGY);
    UpdatePostTagsRequestDto requestDto = new UpdatePostTagsRequestDto(newTags);
    HttpEntity<UpdatePostTagsRequestDto> request = new HttpEntity<>(requestDto);

    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/posts/" + postUuid + "/tags", HttpMethod.PATCH, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldReturnNotFoundWhenPostDoesNotExist() {
    final String username = TestDataSamples.LEANNE_USERNAME;
    final String password = TestDataSamples.LEANNE_PASSWORD;
    final UUID nonExistentUuid = UUID.randomUUID();
    final List<String> newTags = List.of(TestDataSamples.TAG_JAVA, TestDataSamples.TAG_TECHNOLOGY);
    UpdatePostTagsRequestDto requestDto = new UpdatePostTagsRequestDto(newTags);
    final String accessToken = createAccessToken(username, password);
    HttpEntity<UpdatePostTagsRequestDto> request = createAuthenticatedRequestWithAccessToken(accessToken, requestDto);

    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/posts/" + nonExistentUuid + "/tags", HttpMethod.PATCH, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
