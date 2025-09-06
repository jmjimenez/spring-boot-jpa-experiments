package es.jmjg.experiments.infrastructure.controller.tag;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.tag.FindPostsByTag;
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

class TagControllerGetPostsByTagUuidTest extends BaseTagControllerTest {
  @Autowired
  private FindPostsByTag findPostsByTag;

  private UUID testUuid;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    Integer testId = 1;
    Tag testTag = TagFactory.createTag(testUuid, "test-tag");
    testTag.setId(testId);
  }

  @Test
  void shouldFindPostsByTag() throws Exception {
    // Given
    User user = UserFactory.createUser("Test User", "test@example.com", "testuser");
    Post post = PostFactory.createPost(user, UUID.randomUUID(), "Test Post", "Test content");
    List<Post> posts = List.of(post);
    when(findPostsByTag.findByTagUuid(testUuid)).thenReturn(posts);

    // When & Then
    mockMvc.perform(get("/api/tags/{uuid}/posts", testUuid)
        .header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Test Post"))
        .andExpect(jsonPath("$[0].body").value("Test content"));
  }
}
