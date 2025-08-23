package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.post.dto.FindAllPostsResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.PagedResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class PostControllerGetAllIntegrationTest extends BaseControllerIntegration {

  @Test
  void authenticatedUserShouldReturnAllPosts() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);

    ResponseEntity<PagedResponseDto<FindAllPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<PagedResponseDto<FindAllPostsResponseDto>>() {
        });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PagedResponseDto<FindAllPostsResponseDto> pagedResponse = response.getBody();
    assertThat(pagedResponse).isNotNull().satisfies(p -> {
      assertThat(p.getContent()).isNotNull();
      assertThat(p.getContent()).hasSizeGreaterThan(0);
      assertThat(p.getPageNumber()).isEqualTo(0);
      assertThat(p.getPageSize()).isEqualTo(20);
      assertThat(p.getTotalElements()).isGreaterThan(0);
      assertThat(p.getTotalPages()).isGreaterThan(0);
    });
  }

  @Test
  void unauthenticatedUserShouldNotReturnAllPosts() {
    ResponseEntity<PagedResponseDto<FindAllPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.GET,
        null,
        new org.springframework.core.ParameterizedTypeReference<PagedResponseDto<FindAllPostsResponseDto>>() {
        });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PagedResponseDto<FindAllPostsResponseDto> pagedResponse = response.getBody();
    assertThat(pagedResponse).isNotNull().satisfies(p -> {
      assertThat(p.getContent()).isNotNull();
      assertThat(p.getContent()).hasSizeGreaterThan(0);
      assertThat(p.getPageNumber()).isEqualTo(0);
      assertThat(p.getPageSize()).isEqualTo(20);
      assertThat(p.getTotalElements()).isGreaterThan(0);
      assertThat(p.getTotalPages()).isGreaterThan(0);
    });
  }

  @Test
  void shouldReturnAllPostsWithPagination() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

    ResponseEntity<PagedResponseDto<FindAllPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts?page=0&size=5",
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<PagedResponseDto<FindAllPostsResponseDto>>() {
        });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PagedResponseDto<FindAllPostsResponseDto> pagedResponse = response.getBody();
    assertThat(pagedResponse).isNotNull().satisfies(p -> {
      assertThat(p.getContent()).isNotNull();
      assertThat(p.getContent()).hasSizeLessThanOrEqualTo(5);
      assertThat(p.getPageNumber()).isEqualTo(0);
      assertThat(p.getPageSize()).isEqualTo(5);
      assertThat(p.getTotalElements()).isGreaterThan(0);
      assertThat(p.getTotalPages()).isGreaterThan(0);
    });
  }

  @Test
  void shouldReturnAllPostsWithDefaultPagination() {
    ResponseEntity<PagedResponseDto<FindAllPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.GET,
        null,
        new org.springframework.core.ParameterizedTypeReference<PagedResponseDto<FindAllPostsResponseDto>>() {
        });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PagedResponseDto<FindAllPostsResponseDto> pagedResponse = response.getBody();
    assertThat(pagedResponse).isNotNull().satisfies(p -> {
      assertThat(p.getContent()).isNotNull();
      assertThat(p.getContent()).hasSizeLessThanOrEqualTo(20);
      assertThat(p.getPageNumber()).isEqualTo(0);
      assertThat(p.getPageSize()).isEqualTo(20);
      assertThat(p.getTotalElements()).isGreaterThan(0);
      assertThat(p.getTotalPages()).isGreaterThan(0);
    });
  }
}
