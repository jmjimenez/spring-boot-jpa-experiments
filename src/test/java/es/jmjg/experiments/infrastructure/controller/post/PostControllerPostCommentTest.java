package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.post.SavePostComment;
import es.jmjg.experiments.application.post.dto.SavePostCommentDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.JsonSamples;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostControllerPostCommentTest extends BasePostControllerTest {

  @Autowired
  private SavePostComment savePostComment;

  @Test
  void shouldCreateNewPostCommentWhenGivenValidID() throws Exception {
    // Given
    User user = UserFactory.createBasicUser();
    Post post = PostFactory.createPost(user, UUID.randomUUID(), "This is my brand new post", "TEST BODY");
    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);

    PostComment postComment = PostFactory.createPostComment(user, post, "This is a comment");

    when(savePostComment.save(any(SavePostCommentDto.class))).thenReturn(postComment);

    // When and Then
    String requestBody = JsonSamples.createAddPostCommentRequestJson(postComment);
    String expectedResponse = JsonSamples.createAddPostCommentResponseJson(postComment);

    mockMvc
        .perform(post("/api/posts/" + post.getUuid() + "/comments")
            .contentType("application/json")
            .content(requestBody)
            .with(user(userDetails)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/posts/" + post.getUuid() + "/comments/" + postComment.getUuid()))
        .andExpect(content().json(expectedResponse));
  }

  @Test
  void shouldReturnBadRequestWhenPostIsNotFound() throws Exception {
    // Given
    User user = UserFactory.createBasicUser();
    Post post = PostFactory.createPost(user, UUID.randomUUID(), "This is my brand new post", "TEST BODY");
    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);

    PostComment postComment = PostFactory.createPostComment(user, post, "This is a comment");

    when(savePostComment.save(any(SavePostCommentDto.class))).thenThrow(new PostNotFound("Post not found"));

    // When and Then
    String requestBody = JsonSamples.createAddPostCommentRequestJson(postComment);

    mockMvc
      .perform(post("/api/posts/" + post.getUuid() + "/comments")
        .contentType("application/json")
        .content(requestBody)
        .with(user(userDetails)))
      .andExpect(status().isNotFound());
  }
}
