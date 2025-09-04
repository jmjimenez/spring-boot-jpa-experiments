package es.jmjg.experiments.infrastructure.controller.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.post.dto.FindPostByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostRequestDto;
import es.jmjg.experiments.infrastructure.controller.post.dto.SavePostResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class PostControllerPostTestIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldCreateNewPostWhenPostIsValid() {
    final String existingTagName = TestDataSamples.TECHNOLOGY_TAG_NAME;
    final String postTitle = "101 Title";
    final String postBody = "101 Body";

    SavePostRequestDto postDto = new SavePostRequestDto(java.util.UUID.randomUUID(),
        postTitle, postBody, List.of(existingTagName));

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
              assertThat(body.getTags()).hasSize(1);
              assertThat(body.getTags()).extracting("name")
                  .containsExactlyInAnyOrder(existingTagName);
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
        assertThat(fp.getTags()).hasSize(1);
        assertThat(fp.getTags()).extracting("name")
            .containsExactlyInAnyOrder(existingTagName);
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
  void shouldNotCreateNewPostWhenTagNotFound() {
    final String notExistingTagName = "NotExistingTag";
    final String postTitle = "101 Title";
    final String postBody = "101 Body";

    SavePostRequestDto postDto = new SavePostRequestDto(java.util.UUID.randomUUID(),
      postTitle, postBody, List.of(notExistingTagName));

    final String accessToken = createAccessToken(TestDataSamples.LEANNE_USERNAME, TestDataSamples.USER_PASSWORD);
    HttpEntity<SavePostRequestDto> request = createAuthenticatedRequestWithAccessToken(accessToken, postDto);

    final ResponseEntity<SavePostResponseDto> response = restTemplate.exchange(
      "/api/posts",
      HttpMethod.POST,
      request,
      SavePostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
