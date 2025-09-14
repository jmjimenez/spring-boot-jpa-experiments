package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostCommentRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostCommentResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PostControllerPostCommentIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldCreateNewPostCommentWhenPostCommentIsValid() {
    UUID postCommentUuid = UUID.randomUUID();
    String comment = "This is a test comment";
    SavePostCommentRequestDto requestDto = new SavePostCommentRequestDto(postCommentUuid, comment);

    final String accessToken = createAccessToken(TestDataSamples.ERVIN_USERNAME, TestDataSamples.USER_PASSWORD);
    HttpEntity<SavePostCommentRequestDto> request = createAuthenticatedRequestWithAccessToken(accessToken, requestDto);

    final ResponseEntity<SavePostCommentResponseDto> response = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments",
        HttpMethod.POST,
        request,
        SavePostCommentResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    //TODO: Verify Location header is present and correct
//    String locationHeader = response.getHeaders().getFirst("Location");
//    assertThat(locationHeader).isNotNull();
//    assertThat(locationHeader).startsWith("/api/posts/");

    SavePostCommentResponseDto responseDto = response.getBody();
    assertThat(responseDto).isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getId()).isEqualTo(postCommentUuid);
              assertThat(body.getUserId()).isEqualTo(TestDataSamples.ERVIN_UUID);
              assertThat(body.getPostId()).isEqualTo(TestDataSamples.LEANNE_POST_UUID);
              assertThat(body.getComment()).isEqualTo(comment);
              // TODOL Verify the Location header contains the correct UUID
//              assertThat(locationHeader).isEqualTo("/api/posts/" + body.getUuid().toString());
            });
  }

  @Test
  void shouldNotCreateNewPostCommentWhenUserIsNotAuthenticated() {
    UUID postCommentUuid = UUID.randomUUID();
    String comment = "This is a test comment";
    SavePostCommentRequestDto requestDto = new SavePostCommentRequestDto(postCommentUuid, comment);
    HttpEntity<SavePostCommentRequestDto> request = createUnauthenticatedRequest(requestDto);

    final ResponseEntity<SavePostResponseDto> response = restTemplate.exchange(
      "/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments",
        HttpMethod.POST,
        request,
        SavePostResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldNotCreateNewPostWhenValidationFails() {
    UUID postCommentUuid = UUID.randomUUID();
    String comment = "This is a test comment";
    SavePostCommentRequestDto requestDto = new SavePostCommentRequestDto(postCommentUuid, comment);

    HttpEntity<SavePostCommentRequestDto> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD, requestDto);

    final ResponseEntity<SavePostResponseDto> response = restTemplate.exchange(
      "/api/posts/" + UUID.randomUUID() + "/comments",
        HttpMethod.POST,
        request,
        SavePostResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
