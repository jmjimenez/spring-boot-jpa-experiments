package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.DeleteUser;
import es.jmjg.experiments.application.user.dto.DeleteUserDto;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.UserFactory;

class UserControllerDeleteTest extends BaseUserControllerTest {

  @Autowired
  private DeleteUser deleteUser;

  private User testUser;
  private UUID testUuid;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    testUuid = testUser.getUuid();

    reset(deleteUser);
  }

  @Test
  void shouldDeleteUserWhenGivenValidUuid() throws Exception {
    // Given
    doNothing().when(deleteUser).delete(any(DeleteUserDto.class));

    // When & Then
    mockMvc
        .perform(delete("/api/users/" + testUuid)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isNoContent());

    verify(deleteUser, times(1)).delete(any(DeleteUserDto.class));
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
    // Given
    doThrow(new UserNotFound(testUuid)).when(deleteUser).delete(any(DeleteUserDto.class));

    // When & Then
    mockMvc
        .perform(delete("/api/users/" + testUuid)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with uuid: " + testUuid));

    verify(deleteUser, times(1)).delete(any(DeleteUserDto.class));
  }

  @Test
  void shouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
    // Given
    doThrow(new Forbidden("Only admin users can delete users")).when(deleteUser).delete(any(DeleteUserDto.class));

    // When & Then
    mockMvc
        .perform(delete("/api/users/" + testUuid)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403))
        .andExpect(jsonPath("$.error").value("Forbidden"))
        .andExpect(jsonPath("$.message").value("Only admin users can delete users"));

    verify(deleteUser, times(1)).delete(any(DeleteUserDto.class));
  }

  @Test
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
    // When & Then
    mockMvc
        .perform(delete("/api/users/" + testUuid))
        .andExpect(status().isUnauthorized());

    // Verify that the service is not called when authentication fails
    verifyNoInteractions(deleteUser);
  }
}
