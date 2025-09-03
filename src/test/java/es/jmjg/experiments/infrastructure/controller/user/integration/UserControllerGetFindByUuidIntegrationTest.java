package es.jmjg.experiments.infrastructure.controller.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.user.dto.FindUserByUuidResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class UserControllerGetFindByUuidIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldFindUserByUuid() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindUserByUuidResponseDto> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.LEANNE_UUID,
        HttpMethod.GET, request, FindUserByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindUserByUuidResponseDto user = response.getBody();
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
  void shouldReturnNotFoundWhenUserDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindUserByUuidResponseDto> response = restTemplate.exchange(
        "/api/users/" + nonExistentUuid, HttpMethod.GET, request, FindUserByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByUuidDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);
    ResponseEntity<FindUserByUuidResponseDto> response = restTemplate.exchange(
        "/api/users/" + nonExistentUuid, HttpMethod.GET, request, FindUserByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnUnauthorizedWhenNotAuthenticated() {
    ResponseEntity<FindUserByUuidResponseDto> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.LEANNE_UUID,
        HttpMethod.GET, null, FindUserByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldReturnOkWhenNonAdminUserIsAllowed() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD);
    ResponseEntity<FindUserByUuidResponseDto> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.LEANNE_UUID,
        HttpMethod.GET, request, FindUserByUuidResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FindUserByUuidResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getUuid()).isEqualTo(TestDataSamples.LEANNE_UUID);
      assertThat(u.getName()).isEqualTo(TestDataSamples.LEANNE_NAME);
      assertThat(u.getEmail()).isEqualTo(TestDataSamples.LEANNE_EMAIL);
      assertThat(u.getUsername()).isEqualTo(TestDataSamples.LEANNE_USERNAME);
    });
  }

  @Test
  void shouldReturnForbiddenWhenNonAdminUserIsNotAllowed() {
    HttpEntity<String> request = createAuthenticatedRequest(TestDataSamples.ERVIN_USERNAME,
        TestDataSamples.USER_PASSWORD);
    ResponseEntity<FindUserByUuidResponseDto> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.LEANNE_UUID,
        HttpMethod.GET, request, FindUserByUuidResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

}
