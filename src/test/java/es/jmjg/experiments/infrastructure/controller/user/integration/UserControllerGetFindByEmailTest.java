package es.jmjg.experiments.infrastructure.controller.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.user.dto.FindAllUsersResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class UserControllerGetFindByEmailTest extends BaseControllerIntegration {

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
  void shouldReturnUnauthorizedWhenRequestIsNotAuthenticated() {
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/search/email?email=" + TestDataSamples.ERVIN_EMAIL,
        HttpMethod.GET, null, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldReturnForbiddenWhenAuthenticatedUserIsNotAdmin() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/search/email?email=" + TestDataSamples.ERVIN_EMAIL,
        HttpMethod.GET, request, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
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
}
