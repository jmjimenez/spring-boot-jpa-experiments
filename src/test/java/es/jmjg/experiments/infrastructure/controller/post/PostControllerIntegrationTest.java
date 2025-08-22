package es.jmjg.experiments.infrastructure.controller.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.post.dto.FindAllPostsResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.PagedResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SearchPostsResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class PostControllerIntegrationTest extends BaseControllerIntegration {

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

  @Test
  void authenticatedUserShouldReturnPostByUuid() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);

    ResponseEntity<FindPostByUuidResponseDto> response = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.POST_2_UUID,
        HttpMethod.GET,
        request,
        FindPostByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindPostByUuidResponseDto post = response.getBody();
    assertThat(post).isNotNull().satisfies(p -> {
      assertThat(p.getTitle()).isEqualTo(TestDataSamples.POST_2_TITLE);
      assertThat(p.getTags()).isNotNull();
      assertThat(p.getTags()).hasSize(3);
      assertThat(p.getTags()).extracting("name")
          .containsExactlyInAnyOrder(TestDataSamples.TECHNOLOGY_TAG_NAME, TestDataSamples.SPRING_BOOT_TAG_NAME,
              TestDataSamples.JPA_TAG_NAME);
    });
  }

  @Test
  void unauthenticatedUserShouldNotReturnPostByUuid() {
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
          .containsExactlyInAnyOrder(TestDataSamples.TECHNOLOGY_TAG_NAME, TestDataSamples.SPRING_BOOT_TAG_NAME,
              TestDataSamples.JPA_TAG_NAME);
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
  void authenticatedUserShouldSearchPosts() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);

    ResponseEntity<List<SearchPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts/search?q=" + TestDataSamples.SEARCH_TERM_SUNT + "&limit=20",
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<List<SearchPostsResponseDto>>() {
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
  void unauthenticatedUserShouldNotSearchPosts() {
    ResponseEntity<List<SearchPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts/search?q=" + TestDataSamples.SEARCH_TERM_SUNT + "&limit=20",
        HttpMethod.GET,
        null,
        new org.springframework.core.ParameterizedTypeReference<List<SearchPostsResponseDto>>() {
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
  void shouldCreateNewPostWhenPostIsValid() {
    final String existingTagName = TestDataSamples.TECHNOLOGY_TAG_NAME;
    final String newTagName = "integration-test-tag";
    final String postTitle = "101 Title";
    final String postBody = "101 Body";

    SavePostRequestDto postDto = new SavePostRequestDto(java.util.UUID.randomUUID(), 
        postTitle, postBody, List.of(existingTagName, newTagName));

    final String accessToken = createAccessToken(TestDataSamples.LEANNE_USERNAME, TestDataSamples.USER_PASSWORD);
    HttpEntity<SavePostRequestDto> request = createAuthenticatedRequestWithAccessToken(accessToken, postDto);

    final ResponseEntity<SavePostResponseDto> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.POST,
        request,
        SavePostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    // Verify Location header is present and correct
    String locationHeader = response.getHeaders().getFirst("Location");
    assertThat(locationHeader).isNotNull();
    assertThat(locationHeader).startsWith("/api/posts/");

    SavePostResponseDto post = response.getBody();
    assertThat(post).isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getUserId()).isEqualTo(TestDataSamples.LEANNE_UUID);
              assertThat(body.getTitle()).isEqualTo(postTitle);
              assertThat(body.getBody()).isEqualTo(postBody);
              assertThat(body.getTags()).isNotNull();
              assertThat(body.getTags()).hasSize(2);
              assertThat(body.getTags()).extracting("name")
                  .containsExactlyInAnyOrder(existingTagName, newTagName);
              // Verify the Location header contains the correct UUID
              assertThat(locationHeader).isEqualTo("/api/posts/" + body.getUuid().toString());
            });

    // Verify the post can be found and has the expected tags
    assertThat(post).isNotNull().satisfies(p -> {
      HttpEntity<String> getRequest = createAuthenticatedRequestWithAccessToken(accessToken);

      ResponseEntity<FindPostByUuidResponseDto> foundPostResponse = restTemplate.exchange(
          "/api/posts/" + p.getUuid(),
          HttpMethod.GET,
          getRequest,
          FindPostByUuidResponseDto.class);
      assertThat(foundPostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

      FindPostByUuidResponseDto foundPost = foundPostResponse.getBody();
      assertThat(foundPost).isNotNull().satisfies(fp -> {
        assertThat(fp.getTags()).isNotNull();
        assertThat(fp.getTags()).hasSize(2);
        assertThat(fp.getTags()).extracting("name")
            .containsExactlyInAnyOrder(existingTagName, newTagName);
      });
    });
  }

  @Test
  void shouldNotCreateNewPostWhenUserIsNotAuthenticated() {
    new SavePostRequestDto(java.util.UUID.randomUUID(), "", "", null);

    final ResponseEntity<SavePostResponseDto> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.POST,
        null,
        SavePostResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldNotCreateNewPostWhenValidationFails() {
    SavePostRequestDto postDto = new SavePostRequestDto(java.util.UUID.randomUUID(), "",
        "", null);

    HttpEntity<SavePostRequestDto> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD, postDto);

    final ResponseEntity<SavePostResponseDto> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.POST,
        request,
        SavePostResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldUpdatePostWhenPostExists() {
    final String existingTagName = TestDataSamples.TECHNOLOGY_TAG_NAME;
    final String newTagName = "update-test-tag";
    final String updatedTitle = "Updated Title";
    final String updatedBody = "Updated Body";

    UpdatePostRequestDto postDto = new UpdatePostRequestDto(
        updatedTitle, updatedBody,
        List.of(existingTagName, newTagName));

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
              assertThat(p.getTags()).hasSize(2);
              assertThat(p.getTags()).extracting("name")
                  .containsExactlyInAnyOrder(existingTagName, newTagName);
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
      assertThat(p.getTags()).hasSize(2);
      assertThat(p.getTags()).extracting("name")
          .containsExactlyInAnyOrder(existingTagName, newTagName);
    });
  }

  @Test
  void shouldNotUpdatePostWhenUserIsNotAuthenticated() {
    final String existingTagName = TestDataSamples.TECHNOLOGY_TAG_NAME;
    final String newTagName = "update-test-tag";
    final String updatedTitle = "Updated Title";
    final String updatedBody = "Updated Body";

    new UpdatePostRequestDto(
        updatedTitle, updatedBody,
        List.of(existingTagName, newTagName));

    ResponseEntity<UpdatePostResponseDto> response = restTemplate.exchange(
        "/api/posts/" + TestDataSamples.POST_1_UUID, HttpMethod.PUT, null, UpdatePostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldNotUpdatePostWhenUserIsNotOwner() {
    final String existingTagName = TestDataSamples.TECHNOLOGY_TAG_NAME;
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
