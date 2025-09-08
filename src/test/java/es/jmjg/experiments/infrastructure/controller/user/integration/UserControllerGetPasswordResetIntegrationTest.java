package es.jmjg.experiments.infrastructure.controller.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import es.jmjg.experiments.infrastructure.controller.user.dto.PasswordResetResponseDto;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserControllerGetPasswordResetIntegrationTest extends BaseControllerIntegration {

  @Test
  void shouldDeleteUserByUuid() {
    String username = TestDataSamples.LEANNE_USERNAME;
    String userEmail = TestDataSamples.LEANNE_EMAIL;

    ResponseEntity<PasswordResetResponseDto> response = restTemplate.exchange(
      "/api/users/password/" + username + "/" + userEmail + "/reset", HttpMethod.GET, null, PasswordResetResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
