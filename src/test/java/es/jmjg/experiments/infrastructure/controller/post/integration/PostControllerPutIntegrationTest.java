package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class PostControllerPutIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldUpdatePostWhenPostExists() {
    final String existingTagName = TestDataSamples.TAG_TECHNOLOGY;
    final String updatedTitle = "Updated Title";
    final String updatedBody = "Updated Body";

    UpdatePostRequestDto postDto = new UpdatePostRequestDto(
        updatedTitle, updatedBody,
        List.of(existingTagName));

    final String accessToken = createAccessToken(TestDataSamples.ADMIN_USERNAME, TestDataSamples.ADMIN_PASSWORD);
    HttpEntity<UpdatePostRequestDto> request = createAuthenticatedRequestWithAccessToken(accessToken, postDto);

    ResponseEntity<UpdatePostResponseDto> response = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.POST_1_UUID, HttpMethod.PUT, request, UpdatePostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UpdatePostResponseDto post = response.getBody();
    assertThat(post)
        .isNotNull()
        .satisfies(
            p -> {
              assertThat(p.getTitle()).isEqualTo(updatedTitle);
              assertThat(p.getBody()).isEqualTo(updatedBody);
              assertThat(p.getTags()).isNotNull();
              assertThat(p.getTags()).hasSize(1);
              assertThat(p.getTags()).extracting("name")
                  .containsExactlyInAnyOrder(existingTagName);
            });

    HttpEntity<String> getRequest = createAuthenticatedRequestWithAccessToken(accessToken);

    ResponseEntity<FindPostByUuidResponseDto> foundPostResponse = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.POST_1_UUID,
        HttpMethod.GET,
        getRequest,
        FindPostByUuidResponseDto.class);
    assertThat(foundPostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindPostByUuidResponseDto foundPost = foundPostResponse.getBody();
    assertThat(foundPost).isNotNull().satisfies(p -> {
      assertThat(p.getTitle()).isEqualTo(updatedTitle);
      assertThat(p.getBody()).isEqualTo(updatedBody);
      assertThat(p.getTags()).isNotNull();
      assertThat(p.getTags()).hasSize(1);
      assertThat(p.getTags()).extracting("name")
          .containsExactlyInAnyOrder(existingTagName);
    });
  }

  @Test
  void shouldNotUpdatePostWhenUserIsNotAuthenticated() {
    final String existingTagName = TestDataSamples.TAG_TECHNOLOGY;
    final String newTagName = "update-test-tag";
    final String updatedTitle = "Updated Title";
    final String updatedBody = "Updated Body";

    var postDto = new UpdatePostRequestDto(
        updatedTitle, updatedBody,
        List.of(existingTagName, newTagName));
    HttpEntity<UpdatePostRequestDto> request = createUnauthenticatedRequest(postDto);

    ResponseEntity<UpdatePostResponseDto> response = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.POST_1_UUID, HttpMethod.PUT, request, UpdatePostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldNotUpdatePostWhenUserIsNotOwner() {
    final String existingTagName = TestDataSamples.TAG_TECHNOLOGY;
    final String newTagName = "update-test-tag";
    final String updatedTitle = "Updated Title";
    final String updatedBody = "Updated Body";

    UpdatePostRequestDto postDto = new UpdatePostRequestDto(
        updatedTitle, updatedBody,
        List.of(existingTagName, newTagName));

    final String accessToken = createAccessToken(TestDataSamples.ERVIN_USERNAME, TestDataSamples.USER_PASSWORD);
    HttpEntity<UpdatePostRequestDto> request = createAuthenticatedRequestWithAccessToken(accessToken, postDto);

    ResponseEntity<UpdatePostResponseDto> response = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.POST_1_UUID, HttpMethod.PUT, request, UpdatePostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
