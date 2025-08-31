package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.application.user.dto.FindUserByEmailDto;

class UserControllerGetFindByEmailTest extends BaseUserControllerTest {


  @Test
  void shouldFindUserWhenGivenValidEmail() throws Exception {
    // Given
    String email = "test@example.com";
    when(findUserByEmail.findByEmail(any(FindUserByEmailDto.class))).thenReturn(Optional.of(testUser));

    String expectedJson = createFindUserByEmailJsonResponse();

    // When & Then
    mockMvc
        .perform(get("/api/users/search/email").param("email", email)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidEmail() throws Exception {
    // Given
    String invalidEmail = "nonexistent@example.com";
    when(findUserByEmail.findByEmail(any(FindUserByEmailDto.class))).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/api/users/search/email").param("email", invalidEmail)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isNotFound());
  }

}
