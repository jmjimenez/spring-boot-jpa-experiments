package es.jmjg.experiments.infrastructure.controller.tag;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.tag.FindUsersByTag;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class TagControllerGetUsersBySearchTagsTest extends BaseTagControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  private FindUsersByTag findUsersByTag;

  @BeforeEach
  void setUp() {
    UUID testUuid = UUID.randomUUID();
    Integer testId = 1;
    Tag testTag = TagFactory.createTag(testUuid, "test-tag");
    testTag.setId(testId);
  }

  @Test
  void shouldFindUsersByTagName() throws Exception {
    // Given
    User user = UserFactory.createUser("Test User", "test@example.com", "testuser");
    List<User> users = List.of(user);
    when(findUsersByTag.findByTagName("test-tag")).thenReturn(users);

    // When & Then
    mockMvc.perform(get("/api/tags/search/users")
        .param("name", "test-tag")
        .header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].name").value("Test User"));
  }
}
