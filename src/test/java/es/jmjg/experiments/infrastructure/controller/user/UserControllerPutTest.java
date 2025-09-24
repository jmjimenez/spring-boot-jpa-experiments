package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.shared.jsonsample.UserSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.application.user.dto.UpdateUserDto;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.UserFactory;

class UserControllerPutTest extends BaseUserControllerTest {

  @Autowired
  private UpdateUser updateUser;

  private User testUser;
  private User adminUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.generateBasicUserWithPostsAndTags();
    adminUser = UserFactory.createAdminUser();

    reset(updateUser);
  }

  @Test
  void shouldUpdateUserWhenGivenValidData() throws Exception {
    // Given
    testUser.setEmail("updated@example.com");
    testUser.setName("Updated User");
    testUser.setUsername("updateduser");
    when(updateUser.update(any(UpdateUserDto.class))).thenReturn(testUser);

    // When & Then
    String requestBody = UserSamples.createUpdateUserRequestJson(testUser.getUuid());
    String expectedResponse = UserSamples.createUpdateUserResponseJson(testUser.getPosts(), testUser.getUuid());
    mockMvc
        .perform(put("/api/users/" + testUser.getUuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedResponse));

    verify(updateUser, times(1)).update(any(UpdateUserDto.class));
  }

  @Test
  void shouldUpdateUserWhenAdminUserUpdatesAnyUser() throws Exception {
    testUser.setEmail("updated@example.com");
    testUser.setName("Updated User");
    testUser.setUsername("updateduser");
    // Given
    when(updateUser.update(any(UpdateUserDto.class))).thenReturn(testUser);

    // When & Then - Admin user (admin) updating another user (testUuid)
    String requestBody = UserSamples.createUpdateUserRequestJson(testUser.getUuid());
    String expectedResponse = UserSamples.createUpdateUserResponseJson(testUser.getPosts(), testUser.getUuid());
    mockMvc
        .perform(put("/api/users/" + testUser.getUuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedResponse));

    verify(updateUser, times(1)).update(any(UpdateUserDto.class));
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
    // Given
    doThrow(new UserNotFound(testUser.getUuid())).when(updateUser).update(any(UpdateUserDto.class));

    // When & Then
    String requestBody = UserSamples.createUpdateUserRequestJson(testUser.getUuid());
    mockMvc
        .perform(put("/api/users/" + testUser.getUuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id: " + testUser.getUuid()));

    verify(updateUser, times(1)).update(any(UpdateUserDto.class));
  }

  @Test
  void shouldReturnForbiddenWhenUserIsNotAuthorized() throws Exception {
    // Given
    doThrow(new Forbidden("Access denied: only admins or the user themselves can update user data")).when(updateUser)
        .update(any(UpdateUserDto.class));

    // When & Then
    String requestBody = UserSamples.createUpdateUserRequestJson(testUser.getUuid());
    mockMvc
        .perform(put("/api/users/" + testUser.getUuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403))
        .andExpect(jsonPath("$.error").value("Forbidden"))
        .andExpect(
            jsonPath("$.message").value("Access denied: only admins or the user themselves can update user data"));

    verify(updateUser, times(1)).update(any(UpdateUserDto.class));
  }

  @Test
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
    // Given
    String requestBody = UserSamples.createUpdateUserRequestJson(testUser.getUuid());

    // When & Then
    mockMvc
        .perform(put("/api/users/" + testUser.getUuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isUnauthorized());

    // Verify that the service is not called when authentication fails
    verifyNoInteractions(updateUser);
  }
}
