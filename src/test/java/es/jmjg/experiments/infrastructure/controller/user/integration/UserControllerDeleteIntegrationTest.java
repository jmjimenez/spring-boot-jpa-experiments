package es.jmjg.experiments.infrastructure.controller.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class UserControllerDeleteIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldDeleteUserByUuid() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.CHELSEY_UUID, HttpMethod.DELETE, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + nonExistentUuid, HttpMethod.DELETE, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnForbiddenWhenNonAdminUserTriesToDeleteUser() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.CHELSEY_UUID, HttpMethod.DELETE, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.CHELSEY_UUID, HttpMethod.DELETE, null, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
