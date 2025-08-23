package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.application.post.SavePostDto;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.security.JwtUserDetails;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

class PostControllerPostTest extends BasePostControllerTest {

  @Test
  void shouldCreateNewPostWhenGivenValidID() throws Exception {
    User user = UserFactory.createBasicUser();

    JwtUserDetails userDetails = UserFactory.createUserUserDetails(user);

    Post post = PostFactory.createPost(user, UUID.randomUUID(), "This is my brand new post", "TEST BODY");
    post.setId(3);

    when(savePost.save(any(SavePostDto.class))).thenReturn(post);

    String requestBody = createCreatePostRequestJson(post);
    String expectedResponse = createCreatePostResponseJson(post);

    mockMvc
        .perform(post("/api/posts")
            .contentType("application/json")
            .content(requestBody)
            .with(user(userDetails)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/posts/" + post.getUuid().toString()))
        .andExpect(content().json(expectedResponse));
  }
}
