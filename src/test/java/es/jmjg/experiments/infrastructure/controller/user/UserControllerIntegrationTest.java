package es.jmjg.experiments.infrastructure.controller.user;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.user.dto.FindAllUsersResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserRequestDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;

class UserControllerIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldGetAllUsers() {
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/users", Object.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Object responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldFindUserByUuid() {
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.getForEntity("/api/users/" + LEANNE_UUID,
        FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindAllUsersResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getUuid()).isEqualTo(LEANNE_UUID);
      assertThat(u.getName()).isEqualTo(LEANNE_NAME);
      assertThat(u.getEmail()).isEqualTo(LEANNE_EMAIL);
      assertThat(u.getUsername()).isEqualTo(LEANNE_USERNAME);
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  void shouldFindUserByEmail() {
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/email?email=" + ERVIN_EMAIL,
        FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindAllUsersResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getEmail()).isEqualTo(ERVIN_EMAIL);
      assertThat(u.getName()).isEqualTo(ERVIN_NAME);
      assertThat(u.getUsername()).isEqualTo(ERVIN_USERNAME);
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  void shouldFindUserByUsername() {
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/username?username=" + CLEMENTINE_USERNAME,
        FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindAllUsersResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getUsername()).isEqualTo(CLEMENTINE_USERNAME);
      assertThat(u.getName()).isEqualTo(CLEMENTINE_NAME);
      assertThat(u.getEmail()).isEqualTo(CLEMENTINE_EMAIL);
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  void shouldCreateNewUserWhenUserIsValid() {
    SaveUserRequestDto userDto = new SaveUserRequestDto(UUID.randomUUID(), "New User", "new@example.com", "newuser",
        "password123");

    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users", HttpMethod.POST, new HttpEntity<>(userDto), FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    // Verify Location header is present and correct
    String locationHeader = response.getHeaders().getFirst("Location");
    assertThat(locationHeader).isNotNull();
    assertThat(locationHeader).startsWith("/api/users/");

    FindAllUsersResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getName()).isEqualTo("New User");
      assertThat(u.getEmail()).isEqualTo("new@example.com");
      assertThat(u.getUsername()).isEqualTo("newuser");
      assertThat(u.getUuid()).isNotNull();
      assertThat(u.getPosts()).isEmpty();
      assertThat(u.getTags()).isEmpty();
      // Verify the Location header contains the correct UUID
      assertThat(locationHeader).isEqualTo("/api/users/" + u.getUuid().toString());
    });
  }

  @Test
  void shouldUpdateExistingUser() {
    SaveUserRequestDto updateDto = new SaveUserRequestDto(PATRICIA_UUID, "Updated User", "updated@example.com",
        "updateduser", "password123");

    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/" + PATRICIA_UUID, HttpMethod.PUT, new HttpEntity<>(updateDto),
        FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindAllUsersResponseDto updatedUser = response.getBody();
    assertThat(updatedUser).isNotNull().satisfies(u -> {
      assertThat(u.getUuid()).isEqualTo(PATRICIA_UUID);
      assertThat(u.getName()).isEqualTo("Updated User");
      assertThat(u.getEmail()).isEqualTo("updated@example.com");
      assertThat(u.getUsername()).isEqualTo("updateduser");
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  void shouldDeleteUserByUuid() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + CHELSEY_UUID, HttpMethod.DELETE, null, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.getForEntity("/api/users/" + nonExistentUuid,
        FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByUuidDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.getForEntity("/api/users/" + nonExistentUuid,
        FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByEmailDoesNotExist() {
    String nonExistentEmail = "nonexistent@example.com";

    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/email?email=" + nonExistentEmail,
        FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByUsernameDoesNotExist() {
    String nonExistentUsername = "nonexistentuser";

    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/username?username=" + nonExistentUsername,
        FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
