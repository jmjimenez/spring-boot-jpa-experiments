package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.post.dto.SearchPostsResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class PostControllerGetSearchIntegrationTest extends BaseControllerIntegration {

  @Test
  void authenticatedUserShouldSearchPosts() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);

    ResponseEntity<List<SearchPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts/search?q=" + TestDataSamples.SEARCH_TERM_SUNT + "&limit=20",
        HttpMethod.GET,
        request,
      new ParameterizedTypeReference<>() {
      });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<SearchPostsResponseDto> posts = response.getBody();
    assertThat(posts).isNotNull().satisfies(p -> {
      assertThat(p).isNotNull();
      assertThat(p).hasSize(TestDataSamples.EXPECTED_SUNT_SEARCH_COUNT);
      // Verify that all posts have the tags field
      for (SearchPostsResponseDto post : p) {
        assertThat(post.getTags()).isNotNull();
      }
    });
  }

  @Test
  void unauthenticatedUserShouldSearchPosts() {
    ResponseEntity<List<SearchPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts/search?q=" + TestDataSamples.SEARCH_TERM_SUNT + "&limit=20",
        HttpMethod.GET,
        null,
      new ParameterizedTypeReference<>() {
      });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<SearchPostsResponseDto> posts = response.getBody();
    assertThat(posts).isNotNull().satisfies(p -> {
      assertThat(p).isNotNull();
      assertThat(p).hasSize(TestDataSamples.EXPECTED_SUNT_SEARCH_COUNT);
      // Verify that all posts have the tags field
      for (SearchPostsResponseDto post : p) {
        assertThat(post.getTags()).isNotNull();
      }
    });
  }
}
