package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.UpdatePostTags;
import es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostTagsRequestDto;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.JsonSamples;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class PostControllerPatchTagsTest extends BasePostControllerTest {

  @Autowired
  private UpdatePostTags updatePostTags;

  @BeforeEach
  void setUp() {
    reset(updatePostTags);
  }

  @Test
  void shouldPatchTagsSuccessfully() throws Exception {
    // Given
    User user = UserFactory.createBasicUser();
    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);
    Post post = PostFactory.createPost(user, UUID.randomUUID(), "Post title", "Post body");
    Tag tag1 = TagFactory.createTag(UUID.randomUUID(), "tag1");
    Tag tag2 = TagFactory.createTag(UUID.randomUUID(), "tag2");
    post.setTags(List.of(tag1, tag2));
    when(updatePostTags.update(any())).thenReturn(post);

    // When & Then
    UpdatePostTagsRequestDto requestDto = new UpdatePostTagsRequestDto(List.of(tag1.getName(), tag2.getName()));
    String requestBody = JsonSamples.createUpdatePostTagsRequestJson(requestDto);
    mockMvc.perform(patch("/api/posts/" + post.getUuid() + "/tags")
        .contentType("application/json")
        .content(requestBody)
        .with(user(userDetails)))
      .andExpect(status().isOk());
  }

  @Test
  void shouldReturn404WhenTagNotFound() throws Exception {
    // Given
    User user = UserFactory.createBasicUser();
    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);
    UUID postUuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(UUID.randomUUID(), "tagNotFound");
    when(updatePostTags.update(any())).thenThrow(new TagNotFound(tag.getName()));

    // When & Then
    UpdatePostTagsRequestDto requestDto = new UpdatePostTagsRequestDto(List.of(tag.getName()));
    String requestBody = JsonSamples.createUpdatePostTagsRequestJson(requestDto);
    mockMvc.perform(patch("/api/posts/" + postUuid + "/tags")
        .contentType("application/json")
        .content(requestBody)
        .with(user(userDetails)))
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn404WhenPostNotFound() throws Exception {
    // Given
    User user = UserFactory.createBasicUser();
    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);
    UUID postUuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(UUID.randomUUID(), "tagName");
    when(updatePostTags.update(any())).thenThrow(new PostNotFound(postUuid));

    // When & Then
    UpdatePostTagsRequestDto requestDto = new UpdatePostTagsRequestDto(List.of(tag.getName()));
    String requestBody = JsonSamples.createUpdatePostTagsRequestJson(requestDto);
    mockMvc.perform(patch("/api/posts/" + postUuid + "/tags")
        .contentType("application/json")
        .content(requestBody)
        .with(user(userDetails)))
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn403WhenForbidden() throws Exception {
    // Given
    User user = UserFactory.createBasicUser();
    JwtUserDetails userDetails = UserDetailsFactory.createJwtUserDetails(user);
    UUID postUuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(UUID.randomUUID(), "tagName");
    when(updatePostTags.update(any())).thenThrow(Forbidden.class);

    // When & Then
    UpdatePostTagsRequestDto requestDto = new UpdatePostTagsRequestDto(List.of(tag.getName()));
    String requestBody = JsonSamples.createUpdatePostTagsRequestJson(requestDto);
    mockMvc.perform(patch("/api/posts/" + postUuid + "/tags")
        .contentType("application/json")
        .content(requestBody)
        .with(user(userDetails)))
      .andExpect(status().isForbidden());
  }
}
