package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.ResultActions;

import es.jmjg.experiments.domain.entity.Post;

class PostControllerGetAllTest extends BasePostControllerTest {

  @Test
  void shouldFindAllPostsUsingWhen() throws Exception {
    String jsonResponse = createFindAllPostsJsonResponse();

    when(findAllPosts.findAll(any(Pageable.class))).thenReturn(postsPage);

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    verifyJsonResponse(resultActions, jsonResponse);
  }

  @Test
  void shouldFindAllPostsUsingDoReturn() throws Exception {
    String jsonResponse = createFindAllPostsJsonResponse();

    doReturn(postsPage).when(findAllPosts).findAll(any(Pageable.class));

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    verifyJsonResponse(resultActions, jsonResponse);
  }

  @Test
  void shouldFindAllPostsWithPagination() throws Exception {
    String jsonResponse = createFindAllPostsWithPaginationJsonResponse();

    Pageable pageable = PageRequest.of(0, 1);
    Page<Post> singlePostPage = new PageImpl<>(List.of(posts.get(0)), pageable, 2);
    when(findAllPosts.findAll(any(Pageable.class))).thenReturn(singlePostPage);

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts?page=0&size=1"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    verifyJsonResponse(resultActions, jsonResponse);
  }
}
