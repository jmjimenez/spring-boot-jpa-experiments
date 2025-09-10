package es.jmjg.experiments.application.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import es.jmjg.experiments.application.user.dto.PasswordResetDto;
import es.jmjg.experiments.application.user.dto.ResetPasswordDto;
import es.jmjg.experiments.application.user.shared.ResetPasswordKeyService;
import es.jmjg.experiments.domain.shared.exception.InvalidRequest;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ResetPasswordTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private ResetPasswordKeyService resetPasswordKeyService;

  @InjectMocks
  private ResetPassword resetPassword;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
  }

  @Test
  void whenRequestIsCorrect_ShouldUpdateUserPassword() {
    // Given
    String resetKey = "resetKey";
    String newPassword = "newPassword";
    String encodedNewPassword = "encodedNewPassword";
    PasswordResetDto passwordResetDto = new PasswordResetDto(testUser.getUsername(), testUser.getEmail(), LocalDateTime.now().plusHours(10));

    when(resetPasswordKeyService.parseResetKey(any())).thenReturn(passwordResetDto);
    when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
    when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    ResetPasswordDto dto = new ResetPasswordDto(testUser.getUsername(), testUser.getEmail(), resetKey, newPassword);
    resetPassword.reset(dto);

    // Then
    assertThat(testUser.getPassword()).isEqualTo(encodedNewPassword);
  }

  @Test
  void whenUserNotFound_ShouldThrowUserNotFound() {
    // Given
    String invalidUsername = "nonExistentUser";
    String userEmail = "email@test.com";
    String resetKey = "resetKey";
    String newPassword = "newPassword";
    PasswordResetDto passwordResetDto = new PasswordResetDto(invalidUsername, userEmail,
      LocalDateTime.now().plusHours(10));

    when(resetPasswordKeyService.parseResetKey(any())).thenReturn(passwordResetDto);
    when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

    // When & Then
    ResetPasswordDto dto = new ResetPasswordDto(invalidUsername, userEmail, resetKey, newPassword);
    assertThatThrownBy(() -> resetPassword.reset(dto)).isInstanceOf(UserNotFound.class);
    verify(userRepository, never()).save(any(User.class));
  }
  @Test
  void whenResetKeyIsExpired_ShouldThrowInvalidRequest() {
    // Given
    String resetKey = "resetKey";
    String newPassword = "newPassword";
    String originalPassword = testUser.getPassword();
    PasswordResetDto passwordResetDto = new PasswordResetDto(testUser.getUsername(), testUser.getEmail(), LocalDateTime.now().minusHours(10));

    when(resetPasswordKeyService.parseResetKey(any())).thenReturn(passwordResetDto);

    // When
    ResetPasswordDto dto = new ResetPasswordDto(testUser.getUsername(), testUser.getEmail(), resetKey, newPassword);
    assertThatThrownBy(() -> resetPassword.reset(dto)).isInstanceOf(InvalidRequest.class);

    // Then
    assertThat(testUser.getPassword()).isEqualTo(originalPassword);
    verify(userRepository, never()).save(any(User.class));
  }
}
