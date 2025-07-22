package es.jmjg.experiments.infrastructure.controller.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import es.jmjg.experiments.infrastructure.controller.dto.UserRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.UserResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;

class UserControllerIntegrationTest extends BaseControllerIntegration {

  @Autowired
  private TestRestTemplate restTemplate;

  // Sample users from Flyway migration data
  private static final UUID LEANNE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
  private static final String LEANNE_NAME = "Leanne Graham";
  private static final String LEANNE_EMAIL = "leanne.graham@example.com";
  private static final String LEANNE_USERNAME = "leanne_graham";

  private static final String ERVIN_NAME = "Ervin Howell";
  private static final String ERVIN_EMAIL = "ervin.howell@example.com";
  private static final String ERVIN_USERNAME = "ervin_howell";

  private static final String CLEMENTINE_NAME = "Clementine Bauch";
  private static final String CLEMENTINE_EMAIL = "clementine.bauch@example.com";
  private static final String CLEMENTINE_USERNAME = "clementine_bauch";

  private static final UUID PATRICIA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");

  private static final UUID CHELSEY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");

  @Test
  void shouldGetAllUsers() {
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/users", Object.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Object responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldFindUserByUuid() {
    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity("/api/users/" + LEANNE_UUID,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto user = response.getBody();
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
    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity("/api/users/search/email?email=" + ERVIN_EMAIL,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto user = response.getBody();
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
    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/username?username=" + CLEMENTINE_USERNAME,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getUsername()).isEqualTo(CLEMENTINE_USERNAME);
      assertThat(u.getName()).isEqualTo(CLEMENTINE_NAME);
      assertThat(u.getEmail()).isEqualTo(CLEMENTINE_EMAIL);
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  @DirtiesContext
  void shouldCreateNewUserWhenUserIsValid() {
    UserRequestDto userDto = new UserRequestDto(UUID.randomUUID(), "New User", "new@example.com", "newuser");

    ResponseEntity<UserResponseDto> response = restTemplate.exchange(
        "/api/users", HttpMethod.POST, new HttpEntity<>(userDto), UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    UserResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getName()).isEqualTo("New User");
      assertThat(u.getEmail()).isEqualTo("new@example.com");
      assertThat(u.getUsername()).isEqualTo("newuser");
      assertThat(u.getUuid()).isNotNull();
      assertThat(u.getPosts()).isEmpty();
      assertThat(u.getTags()).isEmpty();
    });
  }

  @Test
  @DirtiesContext
  void shouldUpdateExistingUser() {
    UserRequestDto updateDto = new UserRequestDto(PATRICIA_UUID, "Updated User", "updated@example.com", "updateduser");

    ResponseEntity<UserResponseDto> response = restTemplate.exchange(
        "/api/users/" + PATRICIA_UUID, HttpMethod.PUT, new HttpEntity<>(updateDto),
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto updatedUser = response.getBody();
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
  @DirtiesContext
  void shouldDeleteUserByUuid() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + CHELSEY_UUID, HttpMethod.DELETE, null, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity("/api/users/" + nonExistentUuid,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByUuidDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity("/api/users/" + nonExistentUuid,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByEmailDoesNotExist() {
    String nonExistentEmail = "nonexistent@example.com";

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/email?email=" + nonExistentEmail,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByUsernameDoesNotExist() {
    String nonExistentUsername = "nonexistentuser";

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/username?username=" + nonExistentUsername,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
