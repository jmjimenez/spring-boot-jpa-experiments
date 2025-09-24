package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.shared.TagFactory;
import java.util.List;
import java.util.UUID;

import es.jmjg.experiments.domain.post.exception.PostNotFound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.jsonsample.PostSamples;
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
    Tag tag1 = TagFactory.createTag("Tag1", 1);
    Tag tag2 = TagFactory.createTag("Tag2", 2);
    post.setTags(List.of(tag1, tag2));

    PostComment comment1 = PostFactory.createPostComment(user, post, "Comment 1");
    PostComment comment2 = PostFactory.createPostComment(user, post, "Comment 2");
    post.setComments(List.of(comment1, comment2));

    when(findPostByUuid.findByUuid(uuid)).thenReturn(post);
    String json = PostSamples.createFindPostByUuidJsonResponse(post);

    mockMvc
        .perform(get("/api/posts/" + uuid))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }

  @Test
  void shouldThrowPostNotFoundExceptionWhenGivenInvalidUuid() throws Exception {
    UUID uuid = UUID.randomUUID();
    when(findPostByUuid.findByUuid(uuid)).thenThrow(new PostNotFound("Post not found"));

    mockMvc.perform(get("/api/posts/" + uuid)).andExpect(status().isNotFound());
  }
}
