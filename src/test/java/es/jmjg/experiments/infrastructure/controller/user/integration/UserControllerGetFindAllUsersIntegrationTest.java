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

class UserControllerGetFindAllUsersIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldGetAllUsers() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange("/api/users", HttpMethod.GET, request, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindAllUsersResponseDto responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldReturnForbiddenWhenUserIsRegularUser() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange("/api/users", HttpMethod.GET, request, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() {
    ResponseEntity<FindAllUsersResponseDto> response = restTemplate.exchange("/api/users", HttpMethod.GET, null, FindAllUsersResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
