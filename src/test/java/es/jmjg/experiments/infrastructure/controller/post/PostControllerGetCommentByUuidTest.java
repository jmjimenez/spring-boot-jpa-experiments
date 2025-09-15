package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.post.FindPostCommentByUuid;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.JsonSamples;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostControllerGetCommentByUuidTest extends BasePostControllerTest {

  @Autowired
  private FindPostCommentByUuid findPostCommentByUuid;

  @Test
  void shouldFindPostCommentWhenGivenValidUuid() throws Exception {
    User user = UserFactory.createBasicUser();
    Post post = PostFactory.createBasicPost(user);
    PostComment postComment = PostFactory.createPostComment(user, post, "Nice post!", LocalDateTime.now());

    when(findPostCommentByUuid.findByUuid(post.getUuid(), postComment.getUuid())).thenReturn(postComment);
    String json = JsonSamples.createFindPostCommentByUuidJsonResponse(postComment);

    mockMvc
        .perform(get("/api/posts/" + post.getUuid() + "/comments/" + postComment.getUuid()))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }

  @Test
  void shouldThrowPostNotFoundExceptionWhenGivenInvalidUuid() throws Exception {
    UUID uuid = UUID.randomUUID();
    User user = UserFactory.createBasicUser();
    Post post = PostFactory.createBasicPost(user);
    when(findPostCommentByUuid.findByUuid(post.getUuid(), uuid)).thenThrow(new PostNotFound("Post Comment not found"));

    mockMvc.perform(get("/api/posts/" + post.getUuid() + "/comments/" + uuid)).andExpect(status().isNotFound());
  }
}
