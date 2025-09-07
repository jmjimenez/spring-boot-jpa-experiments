package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.JsonSamples;
import es.jmjg.experiments.shared.UserFactory;

class UserControllerPostTest extends BaseUserControllerTest {

  @Autowired
  private SaveUser saveUser;

  private User testUser;
  private User adminUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.generateBasicUserWithPostsAndTags();
    adminUser = UserFactory.createAdminUser();
  }

  @Test
  void shouldCreateNewUserWhenGivenValidData() throws Exception {
    // Given
    when(saveUser.save(any(SaveUserDto.class))).thenReturn(testUser);

    String requestBody = JsonSamples.createSaveUserRequestJson(testUser.getUuid());
    String expectedResponse = JsonSamples.createSaveUserResponseJson(testUser.getUuid());

    // When & Then
    mockMvc
        .perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/users/" + testUser.getUuid().toString()))
        .andExpect(content().json(expectedResponse));
  }

}
