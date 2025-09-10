package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import es.jmjg.experiments.application.user.ResetPassword;
import es.jmjg.experiments.application.user.dto.ResetPasswordDto;
import es.jmjg.experiments.application.user.shared.ResetPasswordKeyService;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class ResetPasswordIntegrationTest extends BaseIntegration {

  @Autowired
  private ResetPassword resetPassword;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Autowired
  private ResetPasswordKeyService resetPasswordKeyService;

  @Test
  @Transactional
  void whenResetRequestIsCorrect_ShouldUpdatePassword() {
    // Given
    User user = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    var newPassword = "newPassword1234";
    var oldPassword = user.getPassword();
    String resetKey = resetPasswordKeyService.generateResetkey(user.getUsername(), user.getEmail());

    // When
    var dto = new ResetPasswordDto(user.getUsername(), user.getEmail(), resetKey, newPassword);
    resetPassword.reset(dto);

    // Then
    User savedUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    assertThat(savedUser.getPassword()).isNotEqualTo(oldPassword);
  }
}
