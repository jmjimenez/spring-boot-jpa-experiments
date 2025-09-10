package es.jmjg.experiments.application.user.shared;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResetPasswordKeyServiceTest {

  private ResetPasswordKeyService resetPasswordKeyService;

  @BeforeEach
  void setUp() {
    resetPasswordKeyService = new ResetPasswordKeyService();
  }

  @Test
  void generateResetkey_ShouldReturnNonEmptyString() {
    // Given
    String username = "testUser";
    String email = "email@test.com";

    // When and Then
    assertThat(resetPasswordKeyService.generateResetkey(username, email)).isNotEmpty();
  }

  @Test
  void parseResetKey_ShouldThrowException_WhenInvalidKey() {
    // Given
    String resetKey = "invalidKey";

    // When and Then
    assertThatThrownBy(() -> resetPasswordKeyService.parseResetKey(resetKey))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("The reset key is not valid");
  }

  @Test
  void parseResetKey_ShouldReturnDto_WhenValidKey() {
    // Given
    String username = "testUser";
    String email = "email@test.com";
    String resetKey = resetPasswordKeyService.generateResetkey(username, email);

    // When
    var dto = resetPasswordKeyService.parseResetKey(resetKey);

    // Then
    assertThat(dto.username()).isEqualTo(username);
    assertThat(dto.email()).isEqualTo(email);
    assertThat(dto.expiryDate()).isAfterOrEqualTo(java.time.LocalDateTime.now());
  }
}
