package es.jmjg.experiments.infrastructure.controller.tag;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.tag.FindTagByPattern;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TagControllerGetByPatternTest extends BaseTagControllerTest {

  @Autowired
  private FindTagByPattern findTagByPattern;

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
  void shouldFindTagsByPattern() throws Exception {
    // Given
    String pattern = "test";
    User user1 = UserFactory.createUser(TestDataSamples.LEANNE_UUID, TestDataSamples.LEANNE_NAME,
        TestDataSamples.LEANNE_EMAIL, TestDataSamples.LEANNE_USERNAME);
    User user2 = UserFactory.createUser(TestDataSamples.ERVIN_UUID, TestDataSamples.ERVIN_NAME,
        TestDataSamples.ERVIN_EMAIL, TestDataSamples.ERVIN_USERNAME);
    Post post1 = PostFactory.createPost(user1, TestDataSamples.POST_1_UUID, "Test Post 1", "Test content 1");
    Post post2 = PostFactory.createPost(user2, TestDataSamples.POST_16_UUID, "Test Post 2", "Test content 2");

    testTag.setUsers(List.of(user1, user2));
    testTag.setPosts(List.of(post1, post2));

    List<Tag> tags = List.of(testTag);
    when(findTagByPattern.findByPattern(pattern)).thenReturn(tags);

    // When & Then
    mockMvc.perform(get("/api/tags/search")
        .param("pattern", pattern)
        .header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].uuid").value(testUuid.toString()))
        .andExpect(jsonPath("$[0].name").value("test-tag"))
        .andExpect(jsonPath("$[0].posts").isArray())
        .andExpect(jsonPath("$[0].posts").value(hasSize(2)))
        .andExpect(jsonPath("$[0].posts[0]").value(TestDataSamples.POST_1_UUID.toString()))
        .andExpect(jsonPath("$[0].posts[1]").value(TestDataSamples.POST_16_UUID.toString()))
        .andExpect(jsonPath("$[0].users").isArray())
        .andExpect(jsonPath("$[0].users").value(hasSize(2)))
        .andExpect(jsonPath("$[0].users[0]").value(TestDataSamples.LEANNE_UUID.toString()))
        .andExpect(jsonPath("$[0].users[1]").value(TestDataSamples.ERVIN_UUID.toString()));
  }
}
