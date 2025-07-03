package es.jmjg.experiments.infrastructure.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import es.jmjg.experiments.application.PostService;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.controller.mapper.PostMapper;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    List<Post> posts;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setUsername("johndoe");

        Post post1 = new Post();
        post1.setId(1);
        post1.setUser(user);
        post1.setTitle("Hello, World!");
        post1.setBody("This is my first post.");

        Post post2 = new Post();
        post2.setId(2);
        post2.setUser(user);
        post2.setTitle("Second Post");
        post2.setBody("This is my second post.");

        posts = List.of(post1, post2);
    }

    @Test
    void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "userId":1,
                        "title":"Hello, World!",
                        "body":"This is my first post."
                    },
                    {
                        "id":2,
                        "userId":1,
                        "title":"Second Post",
                        "body":"This is my second post."
                    }
                ]
                """;

        when(postService.findAll()).thenReturn(posts);

        ResultActions resultActions = mockMvc.perform(get("/api/posts")).andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse,
                resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldFindAllPostsV2() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "userId":1,
                        "title":"Hello, World!",
                        "body":"This is my first post."
                    },
                    {
                        "id":2,
                        "userId":1,
                        "title":"Second Post",
                        "body":"This is my second post."
                    }
                ]
                """;

        doReturn(posts).when(postService).findAll();

        ResultActions resultActions = mockMvc.perform(get("/api/posts")).andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse,
                resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        Post post = new Post();
        post.setId(1);
        post.setUser(user);
        post.setTitle("Test Title");
        post.setBody("Test Body");

        when(postService.findById(1)).thenReturn(Optional.of(post));
        String json = """
                {
                    "id":%d,
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(post.getId(), post.getUser().getId(), post.getTitle(), post.getBody());

        mockMvc.perform(get("/api/posts/1")).andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldNotFindPostWhenGivenInvalidId() throws Exception {
        when(postService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/posts/1")).andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPostWhenGivenValidID() throws Exception {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        Post post = new Post();
        post.setId(3);
        post.setUser(user);
        post.setTitle("This is my brand new post");
        post.setBody("TEST BODY");

        when(postService.save(any(Post.class), eq(1))).thenReturn(post);

        // Request body should be a PostDto (without id, since it's a new post)
        String requestBody = """
                {
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(user.getId(), post.getTitle(), post.getBody());

        // Expected response should include the generated id
        String expectedResponse = """
                {
                    "id":%d,
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(post.getId(), post.getUser().getId(), post.getTitle(), post.getBody());

        mockMvc.perform(post("/api/posts").contentType("application/json").content(requestBody))
                .andExpect(status().isCreated()).andExpect(content().json(expectedResponse));
    }

    @Test
    void shouldUpdatePostWhenGivenValidPost() throws Exception {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        Post updated = new Post();
        updated.setId(1);
        updated.setUser(user);
        updated.setTitle("This is my brand new post");
        updated.setBody("UPDATED BODY");

        when(postService.update(eq(1), any(Post.class), eq(1))).thenReturn(updated);
        String requestBody = """
                {
                    "id":%d,
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(updated.getId(), updated.getUser().getId(), updated.getTitle(),
                updated.getBody());

        mockMvc.perform(put("/api/posts/1").contentType("application/json").content(requestBody))
                .andExpect(status().isOk()).andExpect(content().json(requestBody));
    }

    @Test
    void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPostID() throws Exception {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        Post updated = new Post();
        updated.setId(50);
        updated.setUser(user);
        updated.setTitle("This is my brand new post");
        updated.setBody("UPDATED BODY");

        when(postService.update(eq(999), any(Post.class), eq(1)))
                .thenThrow(new RuntimeException("Post not found with id: 999"));
        String json = """
                {
                    "id":%d,
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(updated.getId(), updated.getUser().getId(), updated.getTitle(),
                updated.getBody());

        mockMvc.perform(put("/api/posts/999").contentType("application/json").content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePostWhenGivenValidID() throws Exception {
        doNothing().when(postService).deleteById(1);

        mockMvc.perform(delete("/api/posts/1")).andExpect(status().isNoContent());

        verify(postService, times(1)).deleteById(1);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public PostService postService() {
            return mock(PostService.class);
        }

        @Bean
        public PostMapper postMapper() {
            return new PostMapper();
        }
    }
}
