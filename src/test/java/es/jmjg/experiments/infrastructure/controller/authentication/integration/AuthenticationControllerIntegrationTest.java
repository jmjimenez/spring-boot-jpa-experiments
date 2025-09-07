package es.jmjg.experiments.infrastructure.controller.authentication.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationRequestDto;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AuthenticationControllerIntegrationTest extends BaseControllerIntegration {

  @Test
  public void whenUserIsValid_shouldReturnAuthenticated() {
    AuthenticationRequestDto authRequest = new AuthenticationRequestDto();
    authRequest.setLogin(TestDataSamples.LEANNE_USERNAME);
    authRequest.setPassword(TestDataSamples.LEANNE_PASSWORD);

    ResponseEntity<AuthenticationResponseDto> authResponse = restTemplate.postForEntity(
      "/authenticate",
      new HttpEntity<>(authRequest),
      AuthenticationResponseDto.class);

    assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(authResponse.getBody()).isNotNull();
    assertThat(authResponse.getBody().getAccessToken()).isNotEmpty();
  }

  @Test
  public void whenUserIsNotValid_shouldReturnAuthenticated() {
    AuthenticationRequestDto authRequest = new AuthenticationRequestDto();
    authRequest.setLogin(TestDataSamples.LEANNE_USERNAME);
    authRequest.setPassword("invalid_password");

    ResponseEntity<AuthenticationResponseDto> authResponse = restTemplate.postForEntity(
      "/authenticate",
      new HttpEntity<>(authRequest),
      AuthenticationResponseDto.class);

    assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  public void whenUserIsMissing_shouldReturnBatRequest() {
    ResponseEntity<AuthenticationResponseDto> authResponse = restTemplate.postForEntity(
      "/authenticate",
      null,
      AuthenticationResponseDto.class);

    assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
