package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.post.dto.FindAllPostsDto;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.shared.JsonSamples;
import es.jmjg.experiments.shared.UserFactory;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.domain.user.entity.User;
import java.util.UUID;

class PostControllerGetAllTest extends BasePostControllerTest {

  @Autowired
  private FindAllPosts findAllPosts;

  protected List<Post> posts;
  protected Page<Post> postsPage;
  protected User testUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();

    Post post1 = PostFactory.createPost(testUser, UUID.randomUUID(), "Hello, World!", "This is my first post.");
    post1.setId(1);

    Post post2 = PostFactory.createPost(testUser, UUID.randomUUID(), "Second Post", "This is my second post.");
    post2.setId(2);

    posts = List.of(post1, post2);
    Pageable pageable = PageRequest.of(0, 20);
    postsPage = new PageImpl<>(posts, pageable, posts.size());
  }

  @Test
  void shouldFindAllPostsUsingWhen() throws Exception {
    String jsonResponse = JsonSamples.createFindAllPostsJsonResponse(posts);

    when(findAllPosts.findAll(any(FindAllPostsDto.class))).thenReturn(postsPage);

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    verifyJsonResponse(resultActions, jsonResponse);
  }

  @Test
  void shouldFindAllPostsUsingDoReturn() throws Exception {
    String jsonResponse = JsonSamples.createFindAllPostsJsonResponse(posts);

    doReturn(postsPage).when(findAllPosts).findAll(any(FindAllPostsDto.class));

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    verifyJsonResponse(resultActions, jsonResponse);
  }

  @Test
  void shouldFindAllPostsWithPagination() throws Exception {
    String jsonResponse = JsonSamples.createFindAllPostsWithPaginationJsonResponse(posts);

    Pageable pageable = PageRequest.of(0, 1);
    Page<Post> singlePostPage = new PageImpl<>(List.of(posts.getFirst()), pageable, 2);
    when(findAllPosts.findAll(any(FindAllPostsDto.class))).thenReturn(singlePostPage);

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts?page=0&size=1"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    verifyJsonResponse(resultActions, jsonResponse);
  }

  @Test
  void shouldReturnEmptyListWhenNoPostsFound() throws Exception {
    String jsonResponse = JsonSamples.createEmptyPostsJsonResponse();

    Pageable pageable = PageRequest.of(0, 20);
    Page<Post> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
    when(findAllPosts.findAll(any(FindAllPostsDto.class))).thenReturn(emptyPage);

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    verifyJsonResponse(resultActions, jsonResponse);
  }
}
