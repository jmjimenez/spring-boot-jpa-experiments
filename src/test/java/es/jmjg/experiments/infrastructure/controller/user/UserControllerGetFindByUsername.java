package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.application.user.dto.FindUserByUsernameDto;

class UserControllerGetFindByUsername extends BaseUserControllerTest {

  @Test
  void shouldFindUserWhenGivenValidUsername() throws Exception {
    // Given
    String username = "testuser";
    when(findUserByUsername.findByUsername(any(FindUserByUsernameDto.class))).thenReturn(Optional.of(testUser));

    String expectedJson = createFindUserByUsernameJsonResponse();

    // When & Then
    mockMvc
        .perform(get("/api/users/search/username").param("username", username)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidUsername() throws Exception {
    // Given
    String invalidUsername = "nonexistentuser";
    when(findUserByUsername.findByUsername(any(FindUserByUsernameDto.class))).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/api/users/search/username").param("username", invalidUsername)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isNotFound());
  }

}
