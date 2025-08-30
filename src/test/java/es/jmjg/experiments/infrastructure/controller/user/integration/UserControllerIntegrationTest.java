package es.jmjg.experiments.infrastructure.controller.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.user.dto.FindAllUsersResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserRequestDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class UserControllerIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldGetAllUsers() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Object> response = restTemplate.exchange("/api/users", HttpMethod.GET, request, Object.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Object responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldFindUserByUuid() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.LEANNE_UUID,
        HttpMethod.GET, request, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindAllUsersResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getUuid()).isEqualTo(TestDataSamples.LEANNE_UUID);
      assertThat(u.getName()).isEqualTo(TestDataSamples.LEANNE_NAME);
      assertThat(u.getEmail()).isEqualTo(TestDataSamples.LEANNE_EMAIL);
      assertThat(u.getUsername()).isEqualTo(TestDataSamples.LEANNE_USERNAME);
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  void shouldFindUserByEmail() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/search/email?email=" + TestDataSamples.ERVIN_EMAIL,
        HttpMethod.GET, request, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindAllUsersResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getEmail()).isEqualTo(TestDataSamples.ERVIN_EMAIL);
      assertThat(u.getName()).isEqualTo(TestDataSamples.ERVIN_NAME);
      assertThat(u.getUsername()).isEqualTo(TestDataSamples.ERVIN_USERNAME);
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  void shouldFindUserByUsername() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/search/username?username=" + TestDataSamples.CLEMENTINE_USERNAME,
        HttpMethod.GET, request, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindAllUsersResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getUsername()).isEqualTo(TestDataSamples.CLEMENTINE_USERNAME);
      assertThat(u.getName()).isEqualTo(TestDataSamples.CLEMENTINE_NAME);
      assertThat(u.getEmail()).isEqualTo(TestDataSamples.CLEMENTINE_EMAIL);
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  void shouldCreateNewUserWhenUserIsValid() {
    SaveUserRequestDto userDto = new SaveUserRequestDto(UUID.randomUUID(), "New User", "new@example.com", "newuser",
        "password123");

    HttpEntity<SaveUserRequestDto> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD, userDto);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users", HttpMethod.POST, request, FindAllUsersResponseDto.class);
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
  void shouldReturnNotFoundWhenUserDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/" + nonExistentUuid, HttpMethod.GET, request, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByUuidDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/" + nonExistentUuid, HttpMethod.GET, request, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByEmailDoesNotExist() {
    String nonExistentEmail = "nonexistent@example.com";

    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/search/email?email=" + nonExistentEmail, HttpMethod.GET, request, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByUsernameDoesNotExist() {
    String nonExistentUsername = "nonexistentuser";

    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/search/username?username=" + nonExistentUsername, HttpMethod.GET, request,
        FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
