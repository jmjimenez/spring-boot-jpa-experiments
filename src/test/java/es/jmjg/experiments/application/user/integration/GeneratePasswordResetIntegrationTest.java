package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import es.jmjg.experiments.application.user.GeneratePasswordReset;
import es.jmjg.experiments.application.user.dto.GeneratePasswordResetDto;
import es.jmjg.experiments.application.user.dto.PasswordResetDto;
import es.jmjg.experiments.application.user.shared.ResetPasswordKeyService;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GeneratePasswordResetIntegrationTest extends BaseIntegration {

  @Autowired
  private GeneratePasswordReset generatePasswordReset;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Autowired
  private ResetPasswordKeyService resetPasswordKeyService;

  @Test
  void whenUserExists_ShouldGeneratePasswordReset() {
    // Given
    User user = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();

    // When
    var dto = new GeneratePasswordResetDto(user.getUsername(), user.getEmail());
    var resetKey = generatePasswordReset.generate(dto);

    // Then
    PasswordResetDto resetDto = resetPasswordKeyService.parseResetKey(resetKey);
    assertThat(resetDto.username()).isEqualTo(user.getUsername());
    assertThat(resetDto.email()).isEqualTo(user.getEmail());
    assertThat(resetDto.expiryDate()).isAfter(LocalDateTime.now());
  }
}
