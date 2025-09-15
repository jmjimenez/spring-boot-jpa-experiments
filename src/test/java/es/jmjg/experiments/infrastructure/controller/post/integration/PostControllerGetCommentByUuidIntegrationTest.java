package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostCommentByUuidResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PostControllerGetCommentByUuidIntegrationTest extends BaseControllerIntegration {

  @Test
  void authenticatedUserShouldReturnPostCommentByUuid() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);

    ResponseEntity<FindPostCommentByUuidResponseDto> response = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID,
        HttpMethod.GET,
        request,
        FindPostCommentByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindPostCommentByUuidResponseDto responseDto = response.getBody();
    assertThat(responseDto).isNotNull().satisfies(p -> assertThat(p.getId()).isEqualTo(TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID));
  }

  @Test
  void unauthenticatedUserShouldReturnPostByUuid() {
    ResponseEntity<FindPostCommentByUuidResponseDto> response = restTemplate.exchange(
      "/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID,
      HttpMethod.GET,
      null,
      FindPostCommentByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindPostCommentByUuidResponseDto responseDto = response.getBody();
    assertThat(responseDto).isNotNull().satisfies(p -> assertThat(p.getId()).isEqualTo(TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID));
  }

  @Test
  void shouldReturnNotFoundForInvalidUuid() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    String randomUuid = java.util.UUID.randomUUID().toString();
    ResponseEntity<FindPostCommentByUuidResponseDto> response = restTemplate.exchange(
      "/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + randomUuid,
        HttpMethod.GET,
        request,
        FindPostCommentByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnBadRequestWhenUuidIsNotProvided() {
    String emptyUuid = "";

    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    ResponseEntity<FindPostCommentByUuidResponseDto> response = restTemplate.exchange(
      "/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + emptyUuid,
        HttpMethod.GET,
        request,
        FindPostCommentByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
