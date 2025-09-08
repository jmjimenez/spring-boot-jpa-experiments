package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.user.GeneratePasswordReset;
import es.jmjg.experiments.application.user.dto.GeneratePasswordResetDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.shared.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserControllerGetPasswordResetTest extends BaseUserControllerTest {

  @Autowired
  private GeneratePasswordReset generatePasswordReset;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();

    reset(generatePasswordReset);
  }

  @Test
  void whenValidUser_shouldGeneratePasswordReset() throws Exception {
    // Given
    var dto = new GeneratePasswordResetDto(testUser.getUsername(), testUser.getEmail());
    String resetKey = "reset-key";

    when(generatePasswordReset.generate(dto)).thenReturn(resetKey);

    // When & Then
    mockMvc
      .perform(get("/api/users/password/" + testUser.getUsername() + "/" + testUser.getEmail() + "/reset"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.resetKey").value(resetKey));
  }

  @Test
  void whenNotValidUser_shouldReturnForbidden() throws Exception {
    // Given
    String invalidUsername = "invalid-username";
    String invalidEmail = "notvalid@emamil.com";
    var dto = new GeneratePasswordResetDto(invalidUsername, invalidEmail);

    when(generatePasswordReset.generate(dto)).thenThrow(new UserNotFound("user not found"));

    // When & Then
    mockMvc
      .perform(get("/api/users/password/" + invalidUsername + "/" + invalidEmail + "/reset"))
      .andExpect(status().isNotFound());
  }
}
