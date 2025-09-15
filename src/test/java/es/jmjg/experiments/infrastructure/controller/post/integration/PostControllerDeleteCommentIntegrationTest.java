package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.post.dto.FindAllPostsResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PostControllerDeleteCommentIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldDeletePostCommentByUuid() {
    // Given
    final UUID postCommentUuid = TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID;

    // When
    final String accessToken = createAccessToken(TestDataSamples.ADMIN_USERNAME, TestDataSamples.ADMIN_PASSWORD);
    HttpEntity<String> request = createAuthenticatedRequestWithAccessToken(accessToken);
    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + postCommentUuid,
        HttpMethod.DELETE,
        request,
        Void.class);

    // Then
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // And: Retrieving the post by UUID should return 404
    HttpEntity<String> getRequest = createAuthenticatedRequestWithAccessToken(accessToken);

    ResponseEntity<FindAllPostsResponseDto> getResponse = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + postCommentUuid,
        HttpMethod.GET,
        getRequest,
        FindAllPostsResponseDto.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldNotDeletePostByUuid_WhenUserIsNotAuthorized() {
    // Given
    final UUID postCommentUuid = TestDataSamples.COMMENT_LEANNE_POST_BY_CLEMENTINE_UUID;

    // When
    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
      "/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + postCommentUuid,
      HttpMethod.DELETE,
      null,
      Void.class);

    // Then
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
