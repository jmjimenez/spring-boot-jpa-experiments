package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.dto.SavePostDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.jsonsample.PostSamples;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class PostControllerPostTest extends BasePostControllerTest {

  @Autowired
  private SavePost savePost;

  @Test
  void shouldCreateNewPostWhenGivenValidID() throws Exception {
    User user = UserFactory.createBasicUser();

    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);

    Post post = PostFactory.createPost(user, UUID.randomUUID(), "This is my brand new post", "TEST BODY");

    when(savePost.save(any(SavePostDto.class))).thenReturn(post);

    String requestBody = PostSamples.createCreatePostRequestJson(post);
    String expectedResponse = PostSamples.createCreatePostResponseJson(post);

    mockMvc
        .perform(post("/api/posts")
            .contentType("application/json")
            .content(requestBody)
            .with(user(userDetails)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/posts/" + post.getUuid().toString()))
        .andExpect(content().json(expectedResponse));
  }

  @Test
  void shouldReturnBadRequestWhenTagIsNotFound() throws Exception {
    User user = UserFactory.createBasicUser();

    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);

    Post post = PostFactory.createPost(user, UUID.randomUUID(), "This is my brand new post", "TEST BODY");

    when(savePost.save(any(SavePostDto.class))).thenThrow(new TagNotFound("Tag not found"));

    String requestBody = PostSamples.createCreatePostRequestJson(post);

    mockMvc
      .perform(post("/api/posts")
        .contentType("application/json")
        .content(requestBody)
        .with(user(userDetails)))
      .andExpect(status().isNotFound());
  }
}
