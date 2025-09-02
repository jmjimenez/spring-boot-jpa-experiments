package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.FindPosts;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.JsonSamples;
import es.jmjg.experiments.shared.PostFactory;

class PostControllerGetSearchTest extends BasePostControllerTest {

  @Autowired
  private FindPosts findPosts;

  @Test
  void shouldSearchPostsWhenGivenValidQuery() throws Exception {
    User user = new User();
    user.setId(1);
    user.setUuid(UUID.randomUUID());
    user.setName("Test User");
    user.setEmail("test@example.com");
    user.setUsername("testuser");
    user.setPassword("encodedPassword123");

    Post searchResult1 = PostFactory.createPost(
        user, UUID.randomUUID(), "Spring Boot Tutorial", "Learn Spring Boot");
    searchResult1.setId(1);

    Post searchResult2 = PostFactory.createPost(user, UUID.randomUUID(), "JPA Best Practices", "Learn JPA");
    searchResult2.setId(2);

    List<Post> searchResults = List.of(searchResult1, searchResult2);

    when(findPosts.find("Spring", 10)).thenReturn(searchResults);

    String expectedJson = JsonSamples.createSearchPostsJsonResponse(searchResults);

    mockMvc
        .perform(get("/api/posts/search?q=Spring&limit=10"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }
}
