package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.JsonSamples;

class UserControllerPostTest extends BaseUserControllerTest {

  @Test
  void shouldCreateNewUserWhenGivenValidData() throws Exception {
    // Given
    User savedUser = es.jmjg.experiments.shared.UserFactory.createUser(testId, testUuid, "Test User",
        "test@example.com", "testuser");
    savedUser.setPosts(testPosts);
    savedUser.setTags(testTags);
    when(saveUser.save(any(SaveUserDto.class))).thenReturn(savedUser);

    String requestBody = JsonSamples.createSaveUserRequestJson(testUuid);
    String expectedResponse = JsonSamples.createSaveUserResponseJson(testUuid);

    // When & Then
    mockMvc
        .perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/users/" + testUuid.toString()))
        .andExpect(content().json(expectedResponse));
  }

}
