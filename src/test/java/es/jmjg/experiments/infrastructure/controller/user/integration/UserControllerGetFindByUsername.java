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

class UserControllerGetFindByUsername extends BaseControllerIntegration {

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
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() {
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange(
        "/api/users/search/username?username=" + TestDataSamples.CLEMENTINE_USERNAME,
        HttpMethod.GET, null, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldFindUserByUsernameWhenAuthenticatedUserIsTheSame() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.CLEMENTINE_USERNAME,
        TestDataSamples.USER_PASSWORD);
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
}
