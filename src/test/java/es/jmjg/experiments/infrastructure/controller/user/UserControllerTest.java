package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;

class UserControllerTest extends BaseUserControllerTest {

  @Test
  void shouldFindUserWhenGivenValidUuid() throws Exception {
    // Given
    when(findUserByUuid.findByUuid(any(FindUserByUuidDto.class))).thenReturn(Optional.of(testUser));

    String expectedJson = createFindUserByUuidJsonResponse();

    // When & Then
    mockMvc
        .perform(get("/api/users/" + testUuid).header("Authorization", "Bearer " + testUser.getUsername()))
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
        .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isNotFound());
  }

}
