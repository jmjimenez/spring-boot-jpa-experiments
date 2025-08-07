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

class PostControllerIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldReturnAllPosts() {
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
    ResponseEntity<PagedResponseDto<FindAllPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts?page=0&size=5",
        HttpMethod.GET,
        null,
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
  void shouldReturnPostByUuid() {
    ResponseEntity<FindPostByUuidResponseDto> response = restTemplate.getForEntity(
        "/api/posts/" + POST_2_UUID, FindPostByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindPostByUuidResponseDto post = response.getBody();
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
    ResponseEntity<FindPostByUuidResponseDto> response = restTemplate.getForEntity("/api/posts/" + randomUuid,
        FindPostByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldSearchPosts() {
    ResponseEntity<List<SearchPostsResponseDto>> response = restTemplate.exchange(
        "/api/posts/search?q=" + SEARCH_TERM_SUNT + "&limit=20",
        HttpMethod.GET,
        null,
        new org.springframework.core.ParameterizedTypeReference<List<SearchPostsResponseDto>>() {
        });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<SearchPostsResponseDto> posts = response.getBody();
    assertThat(posts).isNotNull().satisfies(p -> {
      assertThat(p).isNotNull();
      assertThat(p).hasSize(EXPECTED_SUNT_SEARCH_COUNT);
      // Verify that all posts have the tags field
      for (SearchPostsResponseDto post : p) {
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

    SavePostRequestDto postDto = new SavePostRequestDto(java.util.UUID.randomUUID(), LEANNE_UUID,
        postTitle, postBody, List.of(existingTagName, newTagName));

    final ResponseEntity<SavePostResponseDto> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.POST,
        new HttpEntity<>(postDto),
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
              assertThat(body.getUserId()).isEqualTo(LEANNE_UUID);
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
      ResponseEntity<FindPostByUuidResponseDto> foundPostResponse = restTemplate.getForEntity(
          "/api/posts/" + p.getUuid(), FindPostByUuidResponseDto.class);
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
  void shouldNotCreateNewPostWhenValidationFails() {
    SavePostRequestDto postDto = new SavePostRequestDto(java.util.UUID.randomUUID(), LEANNE_UUID, "", "", null);
    final ResponseEntity<SavePostResponseDto> response = restTemplate.exchange(
        "/api/posts",
        HttpMethod.POST,
        new HttpEntity<>(postDto),
        SavePostResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldUpdatePostWhenPostExists() {
    final String existingTagName = TECHNOLOGY_TAG_NAME;
    final String newTagName = "update-test-tag";
    final String updatedTitle = "Updated Title";
    final String updatedBody = "Updated Body";

    UpdatePostRequestDto postDto = new UpdatePostRequestDto(
        updatedTitle, updatedBody,
        List.of(existingTagName, newTagName));

    ResponseEntity<UpdatePostResponseDto> response = restTemplate.exchange(
        "/api/posts/" + POST_1_UUID, HttpMethod.PUT, new HttpEntity<>(postDto), UpdatePostResponseDto.class);
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

    ResponseEntity<FindPostByUuidResponseDto> foundPostResponse = restTemplate.getForEntity(
        "/api/posts/" + POST_1_UUID, FindPostByUuidResponseDto.class);
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
  void shouldDeletePostByUuid() {
    // Given: Post with UUID POST_3_UUID exists
    final String postUuid = POST_3_UUID.toString();

    // When: Delete the post by UUID
    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
        "/api/posts/" + postUuid,
        HttpMethod.DELETE,
        null,
        Void.class);

    // Then: Should return 204 No Content
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // And: Retrieving the post by UUID should return 404
    ResponseEntity<FindAllPostsResponseDto> getResponse = restTemplate.getForEntity(
        "/api/posts/" + postUuid,
        FindAllPostsResponseDto.class);
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
