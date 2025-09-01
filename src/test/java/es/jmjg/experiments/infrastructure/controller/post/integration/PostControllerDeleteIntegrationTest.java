package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.post.dto.FindAllPostsResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class PostControllerDeleteIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldDeletePostByUuid() {
    final String accessToken = createAccessToken(TestDataSamples.ADMIN_USERNAME, TestDataSamples.ADMIN_PASSWORD);
    HttpEntity<String> request = createAuthenticatedRequestWithAccessToken(accessToken);

    // Given: Post with UUID POST_3_UUID exists
    final String postUuid = TestDataSamples.POST_3_UUID.toString();

    // When: Delete the post by UUID
    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
        "/api/posts/" + postUuid,
        HttpMethod.DELETE,
        request,
        Void.class);

    // Then: Should return 204 No Content
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // And: Retrieving the post by UUID should return 404
    HttpEntity<String> getRequest = createAuthenticatedRequestWithAccessToken(accessToken);

    ResponseEntity<FindAllPostsResponseDto> getResponse = restTemplate.exchange(
        "/api/posts/" + postUuid,
        HttpMethod.GET,
        getRequest,
        FindAllPostsResponseDto.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
