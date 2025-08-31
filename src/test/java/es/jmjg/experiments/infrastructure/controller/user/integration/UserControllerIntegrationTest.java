package es.jmjg.experiments.infrastructure.controller.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.user.dto.FindAllUsersResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class UserControllerIntegrationTest extends BaseControllerIntegration {

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
