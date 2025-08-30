package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.UpdateUserDto;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;

class UserControllerUpdateTest extends BaseUserControllerTest {

  @Test
  void shouldUpdateUserWhenGivenValidData() throws Exception {
    // Given
    User updatedUser = UserFactory.createUser(testId, testUuid, "Updated User",
        "updated@example.com", "updateduser");
    updatedUser.setPosts(testPosts);
    updatedUser.setTags(testTags);
    when(updateUser.update(any(UpdateUserDto.class))).thenReturn(updatedUser);

    String requestBody = createUpdateUserRequestJson();
    String expectedResponse = createUpdateUserResponseJson();

    // When & Then
    mockMvc
        .perform(put("/api/users/" + testUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedResponse));

    verify(updateUser, times(1)).update(any(UpdateUserDto.class));
  }

  @Test
  void shouldUpdateUserWhenAdminUserUpdatesAnyUser() throws Exception {
    // Given
    User updatedUser = UserFactory.createUser(testId, testUuid, "Updated User",
        "updated@example.com", "updateduser");
    updatedUser.setPosts(testPosts);
    updatedUser.setTags(testTags);
    when(updateUser.update(any(UpdateUserDto.class))).thenReturn(updatedUser);

    String requestBody = createUpdateUserRequestJson();
    String expectedResponse = createUpdateUserResponseJson();

    // When & Then - Admin user (admin) updating another user (testUuid)
    mockMvc
        .perform(put("/api/users/" + testUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + TestDataSamples.ADMIN_USERNAME))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedResponse));

    verify(updateUser, times(1)).update(any(UpdateUserDto.class));
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
    // Given
    doThrow(new UserNotFound(testUuid)).when(updateUser).update(any(UpdateUserDto.class));

    String requestBody = createUpdateUserRequestJson();

    // When & Then
    mockMvc
        .perform(put("/api/users/" + testUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with uuid: " + testUuid));

    verify(updateUser, times(1)).update(any(UpdateUserDto.class));
  }

  @Test
  void shouldReturnForbiddenWhenUserIsNotAuthorized() throws Exception {
    // Given
    doThrow(new Forbidden("Access denied: only admins or the user themselves can update user data")).when(updateUser)
        .update(any(UpdateUserDto.class));

    String requestBody = createUpdateUserRequestJson();

    // When & Then
    mockMvc
        .perform(put("/api/users/" + testUuid)
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
    String requestBody = createUpdateUserRequestJson();

    // When & Then
    mockMvc
        .perform(put("/api/users/" + testUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isUnauthorized());

    // Verify that the service is not called when authentication fails
    verifyNoInteractions(updateUser);
  }
}
