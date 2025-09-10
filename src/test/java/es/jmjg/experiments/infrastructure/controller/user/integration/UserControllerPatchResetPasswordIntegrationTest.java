package es.jmjg.experiments.infrastructure.controller.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationRequestDto;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.PasswordResetResponseDto;
import es.jmjg.experiments.infrastructure.controller.user.dto.ResetPasswordRequestDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserControllerPatchResetPasswordIntegrationTest extends BaseControllerIntegration {
  private final String username = TestDataSamples.PATRICIA_USERNAME;
  private final String userEmail = TestDataSamples.PATRICIA_EMAIL;
  @SuppressWarnings("FieldCanBeLocal")
  private final String oldPassword = TestDataSamples.USER_PASSWORD;
  private final String newPassword = "new-secure-password";
  private String resetKey;


  @Test
  void shouldAuthenticateWithNewPasswordAfterReset() {
    shouldAuthenticateWithOldPassword();
    shouldRequestResetKeyWhenUserExists();
    shouldUpdatePasswordWhenRequestIsCorrect();
    shouldAuthenticateWithNewPassword();
  }

  private void shouldRequestResetKeyWhenUserExists() {
    ResponseEntity<PasswordResetResponseDto> response = restTemplate.exchange(
      "/api/users/password/" + username + "/" + userEmail + "/reset", HttpMethod.GET, null, PasswordResetResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    resetKey = response.getBody().getResetKey();
  }

  private void shouldUpdatePasswordWhenRequestIsCorrect() {
    ResetPasswordRequestDto dto = new ResetPasswordRequestDto(resetKey, newPassword);

    HttpEntity<ResetPasswordRequestDto> request = createUnauthenticatedRequest(dto);
    ResponseEntity<Void> response = restTemplate.exchange(
      "/api/users/password/" + username + "/" + userEmail + "/reset", HttpMethod.PATCH, request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private void shouldAuthenticateWithOldPassword() {
    AuthenticationRequestDto authRequest = new AuthenticationRequestDto();
    authRequest.setLogin(username);
    authRequest.setPassword(oldPassword);

    ResponseEntity<AuthenticationResponseDto> authResponse = restTemplate.postForEntity(
      "/authenticate",
      new HttpEntity<>(authRequest),
      AuthenticationResponseDto.class);

    assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(authResponse.getBody()).isNotNull();
    assertThat(authResponse.getBody().getAccessToken()).isNotEmpty();
  }

  private void shouldAuthenticateWithNewPassword() {
    AuthenticationRequestDto authRequest = new AuthenticationRequestDto();
    authRequest.setLogin(username);
    authRequest.setPassword(newPassword);

    ResponseEntity<AuthenticationResponseDto> authResponse = restTemplate.postForEntity(
      "/authenticate",
      new HttpEntity<>(authRequest),
      AuthenticationResponseDto.class);

    assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(authResponse.getBody()).isNotNull();
    assertThat(authResponse.getBody().getAccessToken()).isNotEmpty();
  }
}
