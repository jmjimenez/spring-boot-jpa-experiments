package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.JsonSamples;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

class PostControllerGetByUuidTest extends BasePostControllerTest {

  @Autowired
  private FindPostByUuid findPostByUuid;

  @Test
  void shouldFindPostWhenGivenValidUuid() throws Exception {
    User user = UserFactory.createBasicUser();

    UUID uuid = UUID.randomUUID();
    Post post = PostFactory.createPost(user, uuid, "Test Title", "Test Body");
    post.setId(1);

    when(findPostByUuid.findByUuid(uuid)).thenReturn(Optional.of(post));
    String json = JsonSamples.createFindPostByUuidJsonResponse(post);

    mockMvc
        .perform(get("/api/posts/" + uuid))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }

  @Test
  void shouldNotFindPostWhenGivenInvalidUuid() throws Exception {
    UUID uuid = UUID.randomUUID();
    when(findPostByUuid.findByUuid(uuid)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/posts/" + uuid)).andExpect(status().isNotFound());
  }
}
