package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.domain.user.exception.UserNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.dto.FindUserByUsernameDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.JsonSamples;
import es.jmjg.experiments.shared.UserFactory;

class UserControllerGetFindByUsernameTest extends BaseUserControllerTest {

  @Autowired
  private FindUserByUsername findUserByUsername;

  private User testUser;
  private User adminUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.generateBasicUserWithPostsAndTags();
    adminUser = UserFactory.createAdminUser();
  }

  @Test
  void shouldFindUserWhenGivenValidUsername() throws Exception {
    // Given
    String username = "testuser";
    when(findUserByUsername.findByUsername(any(FindUserByUsernameDto.class))).thenReturn(testUser);

    String expectedJson = JsonSamples.createFindUserByUsernameJsonResponse(testUser.getPosts(), testUser.getUuid());

    // When & Then
    mockMvc
        .perform(get("/api/users/search/username").param("username", username)
            .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidUsername() throws Exception {
    // Given
    String invalidUsername = "nonexistentuser";
    when(findUserByUsername.findByUsername(any(FindUserByUsernameDto.class))).thenThrow(new UserNotFound("User with username " + invalidUsername + " not found"));

    // When & Then
    mockMvc
        .perform(get("/api/users/search/username").param("username", invalidUsername)
            .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isNotFound());
  }

}
