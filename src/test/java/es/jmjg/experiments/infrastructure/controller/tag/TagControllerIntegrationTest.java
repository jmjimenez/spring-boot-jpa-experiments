package es.jmjg.experiments.infrastructure.controller.tag;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByPatternResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.FindTagByUuidResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagRequestDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagResponseDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagRequestDto;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;

class TagControllerIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldFindTagByUuid() {
    // Given
    UUID tagUuid = TECHNOLOGY_UUID;

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.exchange(
        "/api/tags/" + tagUuid,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<FindTagByUuidResponseDto>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindTagByUuidResponseDto tag = response.getBody();
    assertThat(tag).isNotNull().satisfies(t -> {
      assertThat(t.getUuid()).isEqualTo(tagUuid);
      assertThat(t.getName()).isEqualTo("technology");
      assertThat(t.getPosts()).isNotNull();
      assertThat(t.getUsers()).isNotNull();
      // Technology tag should have posts and users based on migration data
      assertThat(t.getPosts()).isNotEmpty();
      assertThat(t.getUsers()).isNotEmpty();
    });
  }

  @Test
  void shouldFindTagByUuidWithNoRelations() {
    // Given
    UUID tagUuid = NOT_USED_UUID;

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.exchange(
        "/api/tags/" + tagUuid,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<FindTagByUuidResponseDto>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindTagByUuidResponseDto tag = response.getBody();
    assertThat(tag).isNotNull().satisfies(t -> {
      assertThat(t.getUuid()).isEqualTo(tagUuid);
      assertThat(t.getName()).isEqualTo("not-used");
      assertThat(t.getPosts()).isNotNull();
      assertThat(t.getUsers()).isNotNull();
      // Not-used tag should have no posts or users
      assertThat(t.getPosts()).isEmpty();
      assertThat(t.getUsers()).isEmpty();
    });
  }

  @SuppressWarnings("null")
  @Test
  void shouldFindTagsByPattern() {
    // Given
    String pattern = "tech";

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<FindTagByPatternResponseDto[]> response = restTemplate.exchange(
        "/api/tags/search?pattern=" + pattern,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<FindTagByPatternResponseDto[]>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    FindTagByPatternResponseDto[] tags = response.getBody();
    assertThat(tags).isNotNull();
    assertThat(tags).isNotEmpty();

    for (FindTagByPatternResponseDto tag : tags) {
      assertThat(tag.getPosts()).isNotNull();
      assertThat(tag.getUsers()).isNotNull();
      // Technology tag should have posts and users based on migration data
      assertThat(tag.getPosts()).isNotEmpty();
      assertThat(tag.getUsers()).isNotEmpty();
    }
  }

  @Test
  void shouldFindTagsByPatternWithNoResults() {
    // Given
    String pattern = "nonexistent";

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<FindTagByPatternResponseDto[]> response = restTemplate.exchange(
        "/api/tags/search?pattern=" + pattern,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<FindTagByPatternResponseDto[]>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    FindTagByPatternResponseDto[] tags = response.getBody();
    assertThat(tags).isNotNull();
    assertThat(tags).isEmpty();
  }

  @Test
  void shouldReturnNotFoundWhenTagByUuidDoesNotExist() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.exchange(
        "/api/tags/" + nonExistentUuid,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<FindTagByUuidResponseDto>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldFindUsersByTag() {
    // Given
    UUID tagUuid = TECHNOLOGY_UUID;

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags/" + tagUuid + "/users",
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<Object>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Object responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldFindPostsByTag() {
    // Given
    UUID tagUuid = TECHNOLOGY_UUID;

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags/" + tagUuid + "/posts",
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<Object>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Object responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldFindUsersByTagName() {
    // Given
    String tagName = "technology";

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags/search/users?name=" + tagName,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<Object>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Object responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldFindPostsByTagName() {
    // Given
    String tagName = "technology";

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags/search/posts?name=" + tagName,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<Object>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Object responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldCreateNewTagWhenTagIsValid() {
    // Given
    SaveTagRequestDto tagDto = new SaveTagRequestDto(UUID.randomUUID(), "new-tag");

    // When
    HttpEntity<SaveTagRequestDto> request = createAuthenticatedRequest(ADMIN_USERNAME, ADMIN_PASSWORD, tagDto);
    ResponseEntity<SaveTagResponseDto> response = restTemplate.exchange(
        "/api/tags", HttpMethod.POST, request, SaveTagResponseDto.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    // Verify Location header is present and correct
    String locationHeader = response.getHeaders().getFirst("Location");
    assertThat(locationHeader).isNotNull();
    assertThat(locationHeader).startsWith("/api/tags/");

    SaveTagResponseDto tag = response.getBody();
    assertThat(tag).isNotNull().satisfies(t -> {
      assertThat(t.getName()).isEqualTo("new-tag");
      assertThat(t.getUuid()).isNotNull();
      // Verify the Location header contains the correct UUID
      assertThat(locationHeader).isEqualTo("/api/tags/" + t.getUuid().toString());
    });
  }

  @Test
  void shouldUpdateExistingTagName() {
    // Given
    UUID tagUuid = JAVA_UUID;
    UpdateTagRequestDto updateDto = new UpdateTagRequestDto(tagUuid, "updated-java");

    // When
    HttpEntity<UpdateTagRequestDto> request = createAuthenticatedRequest(ADMIN_USERNAME, ADMIN_PASSWORD, updateDto);
    ResponseEntity<UpdateTagResponseDto> response = restTemplate.exchange(
        "/api/tags/" + tagUuid, HttpMethod.PUT, request,
        UpdateTagResponseDto.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UpdateTagResponseDto updatedTag = response.getBody();
    assertThat(updatedTag).isNotNull().satisfies(t -> {
      assertThat(t.getUuid()).isEqualTo(tagUuid);
      assertThat(t.getName()).isEqualTo("updated-java");
    });
  }

  @Test
  void shouldReturnErrorWhenDeletingTagUsedByUser() {
    // Given
    UUID tagUuid = DEVELOPER_UUID;

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tags/" + tagUuid, HttpMethod.DELETE, request, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldDeleteUnusedTagByUuid() {
    // Given
    UUID tagUuid = NOT_USED_UUID;

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tags/" + tagUuid, HttpMethod.DELETE, request, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void shouldReturnErrorWhenDeletingTagUsedByPost() {
    // Given
    UUID tagUuid = TECHNOLOGY_UUID;

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tags/" + tagUuid, HttpMethod.DELETE, request, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tags/" + nonExistentUuid, HttpMethod.DELETE, request, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    UpdateTagRequestDto updateDto = new UpdateTagRequestDto(nonExistentUuid, "updated-tag");

    // When
    HttpEntity<UpdateTagRequestDto> request = createAuthenticatedRequest(ADMIN_USERNAME, ADMIN_PASSWORD, updateDto);
    ResponseEntity<UpdateTagResponseDto> response = restTemplate.exchange(
        "/api/tags/" + nonExistentUuid, HttpMethod.PUT, request,
        UpdateTagResponseDto.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenFindingUsersByNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags/" + nonExistentUuid + "/users",
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<Object>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenFindingPostsByNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags/" + nonExistentUuid + "/posts",
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<Object>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenFindingUsersByNonExistentTagName() {
    // Given
    String nonExistentTagName = "nonexistent-tag";

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags/search/users?name=" + nonExistentTagName,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<Object>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenFindingPostsByNonExistentTagName() {
    // Given
    String nonExistentTagName = "nonexistent-tag";

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags/search/posts?name=" + nonExistentTagName,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<Object>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnConflictWhenCreatingTagWithDuplicateName() {
    // Given
    SaveTagRequestDto tagDto1 = new SaveTagRequestDto(UUID.randomUUID(), "duplicate-tag");
    SaveTagRequestDto tagDto2 = new SaveTagRequestDto(UUID.randomUUID(), "duplicate-tag");

    // Create first tag
    HttpEntity<SaveTagRequestDto> request1 = createAuthenticatedRequest(ADMIN_USERNAME, ADMIN_PASSWORD, tagDto1);
    restTemplate.exchange("/api/tags", HttpMethod.POST, request1, SaveTagResponseDto.class);

    // When - Try to create second tag with same name
    HttpEntity<SaveTagRequestDto> request2 = createAuthenticatedRequest(ADMIN_USERNAME, ADMIN_PASSWORD, tagDto2);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags", HttpMethod.POST, request2, Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldReturnConflictWhenCreatingTagWithDuplicateUuid() {
    // Given
    UUID duplicateUuid = UUID.randomUUID();
    SaveTagRequestDto tagDto1 = new SaveTagRequestDto(duplicateUuid, "first-tag");
    SaveTagRequestDto tagDto2 = new SaveTagRequestDto(duplicateUuid, "second-tag");

    // Create first tag
    HttpEntity<SaveTagRequestDto> request1 = createAuthenticatedRequest(ADMIN_USERNAME, ADMIN_PASSWORD, tagDto1);
    restTemplate.exchange("/api/tags", HttpMethod.POST, request1, SaveTagResponseDto.class);

    // When - Try to create second tag with same UUID
    HttpEntity<SaveTagRequestDto> request2 = createAuthenticatedRequest(ADMIN_USERNAME, ADMIN_PASSWORD, tagDto2);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags", HttpMethod.POST, request2, Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldReturnConflictWhenCreatingTagWithExistingNameFromMigration() {
    // Given - Try to create a tag with a name that exists in migration data
    SaveTagRequestDto tagDto = new SaveTagRequestDto(UUID.randomUUID(), "technology");

    // When
    HttpEntity<SaveTagRequestDto> request = createAuthenticatedRequest(ADMIN_USERNAME, ADMIN_PASSWORD, tagDto);
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags", HttpMethod.POST, request, Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @SuppressWarnings("null")
  @Test
  void shouldFindTagsByPatternWithMultipleTags() {
    // Given
    String pattern = "tech";

    // When
    HttpEntity<String> request = generateRequestWithAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    ResponseEntity<FindTagByPatternResponseDto[]> response = restTemplate.exchange(
        "/api/tags/search?pattern=" + pattern,
        HttpMethod.GET,
        request,
        new org.springframework.core.ParameterizedTypeReference<FindTagByPatternResponseDto[]>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    FindTagByPatternResponseDto[] tags = response.getBody();
    assertThat(tags).isNotNull();
    assertThat(tags).isNotEmpty();

    // Verify that each tag has posts and users properties
    for (FindTagByPatternResponseDto tag : tags) {
      assertThat(tag.getPosts()).isNotNull();
      assertThat(tag.getUsers()).isNotNull();
      // All tags should have posts and users based on migration data
      assertThat(tag.getPosts()).isNotEmpty();
      assertThat(tag.getUsers()).isNotEmpty();
    }
  }
}