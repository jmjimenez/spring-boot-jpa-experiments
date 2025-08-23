package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

class PostControllerGetByUuidTest extends BasePostControllerTest {

  @Test
  void shouldFindPostWhenGivenValidUuid() throws Exception {
    User user = UserFactory.createBasicUser();

    UUID uuid = UUID.randomUUID();
    Post post = PostFactory.createPost(user, uuid, "Test Title", "Test Body");
    post.setId(1);

    when(findPostByUuid.findByUuid(uuid)).thenReturn(Optional.of(post));
    String json = createFindPostByUuidJsonResponse(post);

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
