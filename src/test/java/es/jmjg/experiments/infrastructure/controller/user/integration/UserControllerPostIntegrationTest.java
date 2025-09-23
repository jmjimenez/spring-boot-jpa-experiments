package es.jmjg.experiments.infrastructure.controller.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationRequestDto;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserRequestDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.SaveUserResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class UserControllerPostIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldCreateNewUserWhenUserIsValid() {
    // when
    SaveUserRequestDto userDto = new SaveUserRequestDto(UUID.randomUUID(), "New User", "new@example.com", "newuser",
        "password123");

    HttpEntity<SaveUserRequestDto> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD, userDto);
    ResponseEntity<SaveUserResponseDto> response = restTemplate.exchange(
        "/api/users", HttpMethod.POST, request, SaveUserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    // then
    String locationHeader = response.getHeaders().getFirst("Location");
    assertThat(locationHeader).isNotNull();
    assertThat(locationHeader).startsWith("/api/users/");

    SaveUserResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getName()).isEqualTo("New User");
      assertThat(u.getEmail()).isEqualTo("new@example.com");
      assertThat(u.getUsername()).isEqualTo("newuser");
      assertThat(u.getId()).isNotNull();
      // Verify the Location header contains the correct UUID
      assertThat(locationHeader).isEqualTo("/api/users/" + u.getId());
    });

    // Test that the newly created user can authenticate
    AuthenticationRequestDto authRequest = new AuthenticationRequestDto();
    authRequest.setLogin("newuser");
    authRequest.setPassword("password123");

    HttpEntity<AuthenticationRequestDto> authHttpRequest = new HttpEntity<>(authRequest);
    ResponseEntity<AuthenticationResponseDto> authResponse = restTemplate.exchange(
        "/authenticate", HttpMethod.POST, authHttpRequest, AuthenticationResponseDto.class);

    assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(authResponse.getBody()).isNotNull().satisfies(a -> assertThat(a.getAccessToken()).isNotNull().isNotEmpty());
  }

  @Test
  void shouldReturnUnauthorizedWhenNotAuthenticated() {
    // when
    SaveUserRequestDto userDto = new SaveUserRequestDto(UUID.randomUUID(), "New User", "new@example.com", "newuser",
      "password123");

    HttpEntity<SaveUserRequestDto> request = createUnauthenticatedRequest(userDto);

    ResponseEntity<SaveUserResponseDto> response = restTemplate.exchange(
      "/api/users", HttpMethod.POST, request, SaveUserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
