package es.jmjg.experiments.infrastructure.controller.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.jmjg.experiments.infrastructure.controller.user.dto.UpdateUserRequestDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.UpdateUserResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class UserControllerPutIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldUpdateExistingUser() {
    UpdateUserRequestDto updateDto = new UpdateUserRequestDto("Updated User", "updated@example.com");

    HttpEntity<UpdateUserRequestDto> request = createAuthenticatedRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD, updateDto);
    ResponseEntity<UpdateUserResponseDto> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.PATRICIA_UUID, HttpMethod.PUT, request, UpdateUserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UpdateUserResponseDto updatedUser = response.getBody();
    assertThat(updatedUser).isNotNull().satisfies(u -> {
      assertThat(u.getUuid()).isEqualTo(TestDataSamples.PATRICIA_UUID);
      assertThat(u.getName()).isEqualTo("Updated User");
      assertThat(u.getEmail()).isEqualTo("updated@example.com");
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  void shouldAllowUserToUpdateThemselves() {
    UpdateUserRequestDto updateDto = new UpdateUserRequestDto("Self Updated User", "self.updated@example.com");

    HttpEntity<UpdateUserRequestDto> request = createAuthenticatedRequest(TestDataSamples.PATRICIA_USERNAME,
        TestDataSamples.USER_PASSWORD, updateDto);
    ResponseEntity<UpdateUserResponseDto> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.PATRICIA_UUID, HttpMethod.PUT, request, UpdateUserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UpdateUserResponseDto updatedUser = response.getBody();
    assertThat(updatedUser).isNotNull().satisfies(u -> {
      assertThat(u.getUuid()).isEqualTo(TestDataSamples.PATRICIA_UUID);
      assertThat(u.getName()).isEqualTo("Self Updated User");
      assertThat(u.getEmail()).isEqualTo("self.updated@example.com");
      assertThat(u.getPosts()).isNotEmpty();
      assertThat(u.getTags()).isNotEmpty();
    });
  }

  @Test
  void shouldReturnForbiddenWhenNonAdminUserTriesToUpdateOtherUser() {
    UpdateUserRequestDto updateDto = new UpdateUserRequestDto("Unauthorized Update",
        "unauthorized@example.com");

    HttpEntity<UpdateUserRequestDto> request = createAuthenticatedRequest(TestDataSamples.LEANNE_USERNAME,
        TestDataSamples.USER_PASSWORD, updateDto);
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.PATRICIA_UUID, HttpMethod.PUT, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + TestDataSamples.PATRICIA_UUID, HttpMethod.PUT, null, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
