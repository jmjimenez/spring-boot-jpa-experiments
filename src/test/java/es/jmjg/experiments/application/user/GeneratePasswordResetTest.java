package es.jmjg.experiments.application.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import es.jmjg.experiments.application.user.dto.GeneratePasswordResetDto;
import es.jmjg.experiments.application.user.shared.ResetPasswordKeyService;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeneratePasswordResetTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ResetPasswordKeyService resetPasswordKeyService;

  @InjectMocks
  private GeneratePasswordReset generatePasswordReset;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
  }

  @Test
  void whenUserExists_ShouldGenerateResetKey() {
    // Given
    String generatedResetKey = "generatedResetKey";
    when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
    when(resetPasswordKeyService.generateResetkey(testUser.getUsername(), testUser.getEmail())).thenReturn(generatedResetKey);

    // When
    GeneratePasswordResetDto dto = new GeneratePasswordResetDto(testUser.getUsername(), testUser.getEmail());
    assertThat(generatePasswordReset.generate(dto)).isEqualTo(generatedResetKey);
  }

  @Test
  void whenUserDoesNotExist_ShouldThrowUserNotFound() {
    // Given
    when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());

    // When
    GeneratePasswordResetDto dto = new GeneratePasswordResetDto(testUser.getUsername(), testUser.getEmail());
    assertThatThrownBy(() -> generatePasswordReset.generate(dto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void whenUsernameAndEmailDontMatch_ShouldThrowUserNotFound() {
    // Given
    when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

    // When
    GeneratePasswordResetDto dto = new GeneratePasswordResetDto(testUser.getUsername(), "other@email.com");
    assertThatThrownBy(() -> generatePasswordReset.generate(dto)).isInstanceOf(UserNotFound.class);
  }
}
