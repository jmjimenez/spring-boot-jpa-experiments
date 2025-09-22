package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByUuidResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class PostControllerGetByUuidIntegrationTest extends BaseControllerIntegration {

  @Test
  void authenticatedUserShouldReturnPostByUuid() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);

    ResponseEntity<FindPostByUuidResponseDto> response = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.LEANNE_POST_UUID,
        HttpMethod.GET,
        request,
        FindPostByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindPostByUuidResponseDto post = response.getBody();
    assertThat(post).isNotNull().satisfies(p -> {
      assertThat(p.getTitle()).isEqualTo(TestDataSamples.LEANNE_POST_TITLE);
      assertThat(p.getTags()).isNotNull();
      assertThat(p.getTags()).hasSize(3);
      assertThat(p.getTags()).extracting("name")
          .containsExactlyInAnyOrder(TestDataSamples.TAG_TECHNOLOGY, TestDataSamples.TAG_JAVA,
              TestDataSamples.TAG_PROGRAMMING);
      assertThat(p.getPostComments()).hasSize(2);
    });
  }

  @Test
  void unauthenticatedUserShouldReturnPostByUuid() {
    ResponseEntity<FindPostByUuidResponseDto> response = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.POST_2_UUID,
        HttpMethod.GET,
        null,
        FindPostByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindPostByUuidResponseDto post = response.getBody();
    assertThat(post).isNotNull().satisfies(p -> {
      assertThat(p.getTitle()).isEqualTo(TestDataSamples.POST_2_TITLE);
      assertThat(p.getTags()).isNotNull();
      assertThat(p.getTags()).hasSize(3);
      assertThat(p.getTags()).extracting("name")
          .containsExactlyInAnyOrder(TestDataSamples.TAG_TECHNOLOGY, TestDataSamples.TAG_SPRING_BOOT,
              TestDataSamples.TAG_JPA);
    });
  }

  @Test
  void shouldReturnNotFoundForInvalidUuid() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    String randomUuid = java.util.UUID.randomUUID().toString();
    ResponseEntity<FindPostByUuidResponseDto> response = restTemplate.exchange(
        "/api/posts/" + randomUuid,
        HttpMethod.GET,
        request,
        FindPostByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnBadRequestWhenUuidIsNotProvided() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    ResponseEntity<FindPostByUuidResponseDto> response = restTemplate.exchange(
        "/api/posts/",
        HttpMethod.GET,
        request,
        FindPostByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldReturnBadRequestWhenUuidIsMalformed() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    ResponseEntity<FindPostByUuidResponseDto> response = restTemplate.exchange(
        "/api/posts/invalid-uuid-format",
        HttpMethod.GET,
        request,
        FindPostByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
