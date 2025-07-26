package es.jmjg.experiments.infrastructure.controller.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.dto.PagedResponseDto;
import es.jmjg.experiments.infrastructure.controller.dto.PostRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.PostResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;

class PostControllerIntegrationTest extends BaseControllerIntegration {

  // TODO: test telete post
  // TODO: id must be hidden
  @Test
  void shouldReturnAllPosts() {
    ResponseEntity<PagedResponseDto<PostResponseDto>> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.GET,
        null,
        new org.springframework.core.ParameterizedTypeReference<PagedResponseDto<PostResponseDto>>() {
        });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PagedResponseDto<PostResponseDto> pagedResponse = response.getBody();
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
    ResponseEntity<PagedResponseDto<PostResponseDto>> response = restTemplate.exchange(
        "/api/posts?page=0&size=5",
        HttpMethod.GET,
        null,
        new org.springframework.core.ParameterizedTypeReference<PagedResponseDto<PostResponseDto>>() {
        });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PagedResponseDto<PostResponseDto> pagedResponse = response.getBody();
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
  void shouldReturnPostByUuid() {
    ResponseEntity<PostResponseDto> response = restTemplate.getForEntity(
        "/api/posts/" + POST_2_UUID, PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PostResponseDto post = response.getBody();
    assertThat(post).isNotNull().satisfies(p -> {
      assertThat(p.getTitle()).isEqualTo(POST_2_TITLE);
      assertThat(p.getTags()).isNotNull();
      assertThat(p.getTags()).hasSize(3);
      assertThat(p.getTags()).extracting("name")
          .containsExactlyInAnyOrder(TECHNOLOGY_TAG_NAME, SPRING_BOOT_TAG_NAME, JPA_TAG_NAME);
    });
  }

  @Test
  void shouldReturnNotFoundForInvalidUuid() {
    String randomUuid = java.util.UUID.randomUUID().toString();
    ResponseEntity<PostResponseDto> response = restTemplate.getForEntity("/api/posts/" + randomUuid,
        PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldSearchPosts() {
    ResponseEntity<PostResponseDto[]> response = restTemplate.getForEntity(
        "/api/posts/search?q=" + SEARCH_TERM_SUNT + "&limit=20",
        PostResponseDto[].class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PostResponseDto[] posts = response.getBody();
    assertThat(posts).isNotNull().satisfies(p -> {
      assertThat(p).isNotNull();
      assertThat(p).hasSize(EXPECTED_SUNT_SEARCH_COUNT);
      // Verify that all posts have the tags field
      for (PostResponseDto post : p) {
        assertThat(post.getTags()).isNotNull();
      }
    });
  }

  @Test
  void shouldCreateNewPostWhenPostIsValid() {
    final String existingTagName = TECHNOLOGY_TAG_NAME;
    final String newTagName = "integration-test-tag";
    final String postTitle = "101 Title";
    final String postBody = "101 Body";

    PostRequestDto postDto = new PostRequestDto(null, java.util.UUID.randomUUID(), LEANNE_UUID,
        postTitle, postBody, List.of(existingTagName, newTagName));

    ResponseEntity<PostResponseDto> response = restTemplate.exchange(
        "/api/posts", HttpMethod.POST, new HttpEntity<>(postDto), PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    PostResponseDto post = response.getBody();
    assertThat(post).isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getId()).isNotNull();
              assertThat(body.getUserId()).isEqualTo(LEANNE_UUID);
              assertThat(body.getTitle()).isEqualTo(postTitle);
              assertThat(body.getBody()).isEqualTo(postBody);
              assertThat(body.getTags()).isNotNull();
              assertThat(body.getTags()).hasSize(2);
              assertThat(body.getTags()).extracting("name")
                  .containsExactlyInAnyOrder(existingTagName, newTagName);
            });

    // Verify the post can be found and has the expected tags
    assertThat(post).isNotNull().satisfies(p -> {
      ResponseEntity<PostResponseDto> foundPostResponse = restTemplate.getForEntity(
          "/api/posts/" + p.getUuid(), PostResponseDto.class);
      assertThat(foundPostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

      PostResponseDto foundPost = foundPostResponse.getBody();
      assertThat(foundPost).isNotNull().satisfies(fp -> {
        assertThat(fp.getTags()).isNotNull();
        assertThat(fp.getTags()).hasSize(2);
        assertThat(fp.getTags()).extracting("name")
            .containsExactlyInAnyOrder(existingTagName, newTagName);
      });
    });
  }

  @Test
  void shouldNotCreateNewPostWhenValidationFails() {
    PostRequestDto postDto = new PostRequestDto(101, java.util.UUID.randomUUID(), LEANNE_UUID, "", "", null);
    ResponseEntity<PostResponseDto> response = restTemplate.exchange(
        "/api/posts", HttpMethod.POST, new HttpEntity<>(postDto), PostResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldUpdatePostWhenPostExists() {
    final String existingTagName = TECHNOLOGY_TAG_NAME;
    final String newTagName = "update-test-tag";
    final String updatedTitle = "Updated Title";
    final String updatedBody = "Updated Body";

    PostRequestDto postDto = new PostRequestDto(
        null, java.util.UUID.randomUUID(), LEANNE_UUID, updatedTitle, updatedBody,
        List.of(existingTagName, newTagName));

    ResponseEntity<PostResponseDto> response = restTemplate.exchange(
        "/api/posts/1", HttpMethod.PUT, new HttpEntity<>(postDto), PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PostResponseDto post = response.getBody();
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
  }

  @Test
  void shouldDeletePostById() {
    // Given: Post with ID 2 exists (POST_2_UUID)
    final int postId = 2;
    final String postUuid = POST_2_UUID.toString();

    // When: Delete the post by ID
    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
        "/api/posts/" + postId,
        HttpMethod.DELETE,
        null,
        Void.class);

    // Then: Should return 204 No Content
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // And: Retrieving the post by UUID should return 404
    ResponseEntity<PostResponseDto> getResponse = restTemplate.getForEntity(
        "/api/posts/" + postUuid,
        PostResponseDto.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
