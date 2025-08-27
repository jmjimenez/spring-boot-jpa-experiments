package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class PostControllerPutTest extends BasePostControllerTest {

  @Test
  void shouldUpdatePostWhenGivenValidPost() throws Exception {
    User user = UserFactory.createBasicUser();

    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);

    Post updated = PostFactory.createPost(
        user, UUID.randomUUID(), "This is my brand new post", "UPDATED BODY");
    updated.setId(1);

    when(updatePost.update(any())).thenReturn(updated);

    String requestBody = createUpdatePostRequestJson(updated);

    mockMvc
        .perform(put("/api/posts/" + TestDataSamples.POST_2_UUID)
            .contentType("application/json")
            .content(requestBody)
            .with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(content().json(requestBody + ",\"tags\":[]"));
  }

  @Test
  void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPostUUID() throws Exception {
    User user = UserFactory.createBasicUser();

    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);

    Post updated = new Post();
    updated.setId(50);
    updated.setUuid(UUID.randomUUID());
    updated.setUser(user);
    updated.setTitle("This is my brand new post");
    updated.setBody("UPDATED BODY");

    UUID nonExistentUuid = UUID.randomUUID();
    when(updatePost.update(any()))
        .thenThrow(new PostNotFound("Post not found with uuid: " + nonExistentUuid));
    String json = createUpdatePostRequestJson(updated);

    mockMvc
        .perform(put("/api/posts/" + nonExistentUuid)
            .contentType("application/json")
            .content(json)
            .with(user(userDetails)))
        .andExpect(status().isNotFound());
  }
}
