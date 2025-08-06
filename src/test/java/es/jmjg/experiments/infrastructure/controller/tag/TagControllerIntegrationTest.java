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
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.getForEntity("/api/tags/" + tagUuid,
        FindTagByUuidResponseDto.class);

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
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.getForEntity("/api/tags/" + tagUuid,
        FindTagByUuidResponseDto.class);

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
    ResponseEntity<FindTagByPatternResponseDto[]> response = restTemplate.getForEntity("/api/tags/search?pattern=" + pattern,
        FindTagByPatternResponseDto[].class);

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
    ResponseEntity<FindTagByPatternResponseDto[]> response = restTemplate.getForEntity("/api/tags/search?pattern=" + pattern,
        FindTagByPatternResponseDto[].class);

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
    ResponseEntity<FindTagByUuidResponseDto> response = restTemplate.getForEntity("/api/tags/" + nonExistentUuid,
        FindTagByUuidResponseDto.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldFindUsersByTag() {
    // Given
    UUID tagUuid = TECHNOLOGY_UUID;

    // When
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/tags/" + tagUuid + "/users",
        Object.class);

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
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/tags/" + tagUuid + "/posts",
        Object.class);

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
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/tags/search/users?name=" + tagName,
        Object.class);

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
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/tags/search/posts?name=" + tagName,
        Object.class);

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
    ResponseEntity<SaveTagResponseDto> response = restTemplate.exchange(
        "/api/tags", HttpMethod.POST, new HttpEntity<>(tagDto), SaveTagResponseDto.class);

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
    ResponseEntity<UpdateTagResponseDto> response = restTemplate.exchange(
        "/api/tags/" + tagUuid, HttpMethod.PUT, new HttpEntity<>(updateDto),
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
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tags/" + tagUuid, HttpMethod.DELETE, null, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldDeleteUnusedTagByUuid() {
    // Given
    UUID tagUuid = NOT_USED_UUID;

    // When
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tags/" + tagUuid, HttpMethod.DELETE, null, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void shouldReturnErrorWhenDeletingTagUsedByPost() {
    // Given
    UUID tagUuid = TECHNOLOGY_UUID;

    // When
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tags/" + tagUuid, HttpMethod.DELETE, null, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tags/" + nonExistentUuid, HttpMethod.DELETE, null, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    UpdateTagRequestDto updateDto = new UpdateTagRequestDto(nonExistentUuid, "updated-tag");

    // When
    ResponseEntity<UpdateTagResponseDto> response = restTemplate.exchange(
        "/api/tags/" + nonExistentUuid, HttpMethod.PUT, new HttpEntity<>(updateDto),
        UpdateTagResponseDto.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenFindingUsersByNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/tags/" + nonExistentUuid + "/users",
        Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenFindingPostsByNonExistentTag() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/tags/" + nonExistentUuid + "/posts",
        Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenFindingUsersByNonExistentTagName() {
    // Given
    String nonExistentTagName = "nonexistent-tag";

    // When
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/tags/search/users?name=" + nonExistentTagName,
        Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenFindingPostsByNonExistentTagName() {
    // Given
    String nonExistentTagName = "nonexistent-tag";

    // When
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/tags/search/posts?name=" + nonExistentTagName,
        Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnConflictWhenCreatingTagWithDuplicateName() {
    // Given
    SaveTagRequestDto tagDto1 = new SaveTagRequestDto(UUID.randomUUID(), "duplicate-tag");
    SaveTagRequestDto tagDto2 = new SaveTagRequestDto(UUID.randomUUID(), "duplicate-tag");

    // Create first tag
    restTemplate.exchange("/api/tags", HttpMethod.POST, new HttpEntity<>(tagDto1), SaveTagResponseDto.class);

    // When - Try to create second tag with same name
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags", HttpMethod.POST, new HttpEntity<>(tagDto2), Object.class);

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
    restTemplate.exchange("/api/tags", HttpMethod.POST, new HttpEntity<>(tagDto1), SaveTagResponseDto.class);

    // When - Try to create second tag with same UUID
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags", HttpMethod.POST, new HttpEntity<>(tagDto2), Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void shouldReturnConflictWhenCreatingTagWithExistingNameFromMigration() {
    // Given - Try to create a tag with a name that exists in migration data
    SaveTagRequestDto tagDto = new SaveTagRequestDto(UUID.randomUUID(), "technology");

    // When
    ResponseEntity<Object> response = restTemplate.exchange(
        "/api/tags", HttpMethod.POST, new HttpEntity<>(tagDto), Object.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @SuppressWarnings("null")
  @Test
  void shouldFindTagsByPatternWithMultipleTags() {
    // Given
    String pattern = "tech";

    // When
    ResponseEntity<FindTagByPatternResponseDto[]> response = restTemplate.getForEntity("/api/tags/search?pattern=" + pattern,
        FindTagByPatternResponseDto[].class);

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