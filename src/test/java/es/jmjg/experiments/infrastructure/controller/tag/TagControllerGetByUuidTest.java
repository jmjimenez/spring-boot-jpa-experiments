package es.jmjg.experiments.infrastructure.controller.tag;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.tag.FindTagByUuid;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TagControllerGetByUuidTest extends BaseTagControllerTest {
  @Autowired
  private FindTagByUuid findTagByUuid;

  private Tag testTag;
  private UUID testUuid;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    Integer testId = 1;
    testTag = TagFactory.createTag(testUuid, "test-tag");
    testTag.setId(testId);
  }

  @Test
  void shouldFindTagByUuid() throws Exception {
    // Given - Using existing migration test data
    User user1 = UserFactory.createUser(TestDataSamples.LEANNE_UUID, TestDataSamples.LEANNE_NAME,
      TestDataSamples.LEANNE_EMAIL, TestDataSamples.LEANNE_USERNAME);
    User user2 = UserFactory.createUser(TestDataSamples.ERVIN_UUID, TestDataSamples.ERVIN_NAME,
      TestDataSamples.ERVIN_EMAIL, TestDataSamples.ERVIN_USERNAME);
    Post post1 = PostFactory.createPost(user1, TestDataSamples.POST_1_UUID,
      TestDataSamples.POST_1_TITLE, "Test content 1");
    Post post2 = PostFactory.createPost(user2, TestDataSamples.POST_16_UUID, TestDataSamples.ERVIN_POST_TITLE,
      "Test content 2");

    testTag.setUsers(List.of(user1, user2));
    testTag.setPosts(List.of(post1, post2));

    when(findTagByUuid.findByUuid(testUuid)).thenReturn(testTag);

    // When & Then
    mockMvc.perform(get("/api/tags/{uuid}", testUuid).header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
      .andExpect(jsonPath("$.name").value("test-tag"))
      .andExpect(jsonPath("$.posts").isArray())
      .andExpect(jsonPath("$.posts").value(hasSize(2)))
      .andExpect(jsonPath("$.posts[0]").value(TestDataSamples.POST_1_UUID.toString()))
      .andExpect(jsonPath("$.posts[1]").value(TestDataSamples.POST_16_UUID.toString()))
      .andExpect(jsonPath("$.users").isArray())
      .andExpect(jsonPath("$.users").value(hasSize(2)))
      .andExpect(jsonPath("$.users[0]").value(TestDataSamples.LEANNE_UUID.toString()))
      .andExpect(jsonPath("$.users[1]").value(TestDataSamples.ERVIN_UUID.toString()));
  }

  @Test
  void shouldFindTagByUuidWithNoRelations() throws Exception {
    // Given
    testTag.setUsers(List.of());
    testTag.setPosts(List.of());

    when(findTagByUuid.findByUuid(testUuid)).thenReturn(testTag);

    // When & Then
    mockMvc.perform(get("/api/tags/{uuid}", testUuid).header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
      .andExpect(jsonPath("$.name").value("test-tag"))
      .andExpect(jsonPath("$.posts").isArray())
      .andExpect(jsonPath("$.posts").value(hasSize(0)))
      .andExpect(jsonPath("$.users").isArray())
      .andExpect(jsonPath("$.users").value(hasSize(0)));
  }
}
