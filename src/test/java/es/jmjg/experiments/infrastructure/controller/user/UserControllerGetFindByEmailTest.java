package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.shared.jsonsample.UserSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.dto.FindUserByEmailDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.UserFactory;

class UserControllerGetFindByEmailTest extends BaseUserControllerTest {

  @Autowired
  private FindUserByEmail findUserByEmail;

  private User testUser;
  private User adminUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.generateBasicUserWithPostsAndTags();
    adminUser = UserFactory.createAdminUser();
  }

  @Test
  void shouldFindUserWhenGivenValidEmail() throws Exception {
    // Given
    String email = "test@example.com";
    when(findUserByEmail.findByEmail(any(FindUserByEmailDto.class))).thenReturn(testUser);

    String expectedJson = UserSamples.createFindUserByEmailJsonResponse(testUser.getPosts(), testUser.getUuid());

    // When & Then
    mockMvc
        .perform(get("/api/users/search/email").param("email", email)
            .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidEmail() throws Exception {
    // Given
    String invalidEmail = "nonexistent@example.com";
    when(findUserByEmail.findByEmail(any(FindUserByEmailDto.class))).thenThrow(UserNotFound.class);

    // When & Then
    mockMvc
        .perform(get("/api/users/search/email").param("email", invalidEmail)
            .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isNotFound());
  }

}
