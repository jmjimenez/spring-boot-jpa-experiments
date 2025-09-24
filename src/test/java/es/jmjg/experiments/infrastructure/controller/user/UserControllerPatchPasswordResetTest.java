package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.user.ResetPassword;
import es.jmjg.experiments.application.user.dto.ResetPasswordDto;
import es.jmjg.experiments.domain.shared.exception.InvalidRequest;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.shared.UserFactory;
import es.jmjg.experiments.shared.jsonsample.UserSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class UserControllerPatchPasswordResetTest extends BaseUserControllerTest {

  @Autowired
  private ResetPassword resetPassword;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();

    reset(resetPassword);
  }

  @Test
  void shouldReturnOkWhenRequestIsCorrect() throws Exception {
    // Given
    String resetKey = "valid-reset-key";
    String newPassword = "new-password";

    doNothing().when(resetPassword).reset(any(ResetPasswordDto.class));

    // When & Then
    String requestBody = UserSamples.createResetPasswordRequestJson(resetKey, newPassword);

    mockMvc
      .perform(patch("/api/users/password/" + testUser.getUsername() + "/" + testUser.getEmail() + "/reset")
      .contentType(MediaType.APPLICATION_JSON)
      .content(requestBody))
      .andExpect(status().isNoContent());

    verify(resetPassword, times(1)).reset(any(ResetPasswordDto.class));
  }

  @Test
  void shouldReturnBadRequestWhenInvalidRequest() throws Exception {
    // Given
    String resetKey = "valid-reset-key";
    String newPassword = "new-password";

    doThrow(new InvalidRequest("invalid request")).when(resetPassword).reset(any(ResetPasswordDto.class));

    // When & Then
    String requestBody = UserSamples.createResetPasswordRequestJson(resetKey, newPassword);

    mockMvc
      .perform(patch("/api/users/password/" + testUser.getUsername() + "/" + testUser.getEmail() + "/reset")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isBadRequest());

    verify(resetPassword, times(1)).reset(any(ResetPasswordDto.class));
  }

  @Test
  void shouldReturnNotFoundWhenUserIsNotFound() throws Exception {
    // Given
    String resetKey = "valid-reset-key";
    String newPassword = "new-password";

    doThrow(new UserNotFound("user not found")).when(resetPassword).reset(any(ResetPasswordDto.class));

    // When & Then
    String requestBody = UserSamples.createResetPasswordRequestJson(resetKey, newPassword);

    mockMvc
      .perform(patch("/api/users/password/" + testUser.getUsername() + "/" + testUser.getEmail() + "/reset")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isNotFound());

    verify(resetPassword, times(1)).reset(any(ResetPasswordDto.class));
  }
}
