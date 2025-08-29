package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.application.user.dto.DeleteUserDto;

class UserControllerDeleteTest extends BaseUserControllerTest {

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
}
