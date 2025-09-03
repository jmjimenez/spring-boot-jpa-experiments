package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.JsonSamples;
import es.jmjg.experiments.shared.UserFactory;

class UserControllerGetFindByUuidTest extends BaseUserControllerTest {

  @Autowired
  private FindUserByUuid findUserByUuid;

  private User testUser;
  private User adminUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.generateBasicUserWithPostsAndTags();
    adminUser = UserFactory.createAdminUser();
  }

  @Test
  void shouldFindUserWhenGivenValidUuid() throws Exception {
    // Given
    when(findUserByUuid.findByUuid(any(FindUserByUuidDto.class))).thenReturn(Optional.of(testUser));

    String expectedJson = JsonSamples.createFindUserByUuidJsonResponse(testUser.getPosts(), testUser.getUuid());

    // When & Then
    mockMvc
        .perform(get("/api/users/" + testUser.getUuid()).header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidUuid() throws Exception {
    // Given
    java.util.UUID invalidUuid = java.util.UUID.randomUUID();
    when(findUserByUuid.findByUuid(any(FindUserByUuidDto.class))).thenReturn(Optional.empty());

    // When & Then
    mockMvc.perform(get("/api/users/" + invalidUuid)
        .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldThrowForbiddenWhenGivenNotAllowedUser() throws Exception {
    // Given
    java.util.UUID otherUserUuid = java.util.UUID.randomUUID();
    when(findUserByUuid.findByUuid(any(FindUserByUuidDto.class)))
        .thenThrow(new Forbidden(
            "Access denied: only admins or the user themselves can view user data"));

    // When & Then
    mockMvc.perform(get("/api/users/" + otherUserUuid)
        .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isForbidden());
  }

}
