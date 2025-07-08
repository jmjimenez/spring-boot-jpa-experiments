package es.jmjg.experiments.infrastructure.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import es.jmjg.experiments.application.FindPosts;
import es.jmjg.experiments.application.PostService;
import es.jmjg.experiments.application.SavePost;
import es.jmjg.experiments.application.UpdatePost;
import es.jmjg.experiments.application.exception.PostNotFound;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.controller.mapper.PostMapper;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    private FindPosts findPosts;

    @Autowired
    private UpdatePost updatePost;

    @Autowired
    private SavePost savePost;

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
        post1.setUuid(UUID.randomUUID());
        post1.setUser(user);
        post1.setTitle("Hello, World!");
        post1.setBody("This is my first post.");

        Post post2 = new Post();
        post2.setId(2);
        post2.setUuid(UUID.randomUUID());
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
                        "uuid":"%s",
                        "userId":1,
                        "title":"Hello, World!",
                        "body":"This is my first post."
                    },
                    {
                        "id":2,
                        "uuid":"%s",
                        "userId":1,
                        "title":"Second Post",
                        "body":"This is my second post."
                    }
                ]
                """.formatted(posts.get(0).getUuid(), posts.get(1).getUuid());

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
                        "uuid":"%s",
                        "userId":1,
                        "title":"Hello, World!",
                        "body":"This is my first post."
                    },
                    {
                        "id":2,
                        "uuid":"%s",
                        "userId":1,
                        "title":"Second Post",
                        "body":"This is my second post."
                    }
                ]
                """.formatted(posts.get(0).getUuid(), posts.get(1).getUuid());

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
        post.setUuid(UUID.randomUUID());
        post.setUser(user);
        post.setTitle("Test Title");
        post.setBody("Test Body");

        when(postService.findById(1)).thenReturn(Optional.of(post));
        String json = """
                {
                    "id":%d,
                    "uuid":"%s",
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(post.getId(), post.getUuid(), post.getUser().getId(), post.getTitle(),
                post.getBody());

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
        post.setUuid(UUID.randomUUID());
        post.setUser(user);
        post.setTitle("This is my brand new post");
        post.setBody("TEST BODY");

        when(savePost.save(any(Post.class), eq(1))).thenReturn(post);

        // Request body should be a PostDto (without id, since it's a new post)
        String requestBody = """
                {
                    "uuid":"%s",
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(post.getUuid(), user.getId(), post.getTitle(), post.getBody());

        // Expected response should include the generated id
        String expectedResponse = """
                {
                    "id":%d,
                    "uuid":"%s",
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(post.getId(), post.getUuid(), post.getUser().getId(), post.getTitle(),
                post.getBody());

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
        updated.setUuid(UUID.randomUUID());
        updated.setUser(user);
        updated.setTitle("This is my brand new post");
        updated.setBody("UPDATED BODY");

        when(updatePost.update(eq(1), any(Post.class), eq(1))).thenReturn(updated);
        String requestBody = """
                {
                    "id":%d,
                    "uuid":"%s",
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(updated.getId(), updated.getUuid(), updated.getUser().getId(),
                updated.getTitle(), updated.getBody());

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
        updated.setUuid(UUID.randomUUID());
        updated.setUser(user);
        updated.setTitle("This is my brand new post");
        updated.setBody("UPDATED BODY");

        when(updatePost.update(eq(999), any(Post.class), eq(1)))
                .thenThrow(new PostNotFound("Post not found with id: 999"));
        String json = """
                {
                    "id":%d,
                    "uuid":"%s",
                    "userId":%d,
                    "title":"%s",
                    "body":"%s"
                }
                """.formatted(updated.getId(), updated.getUuid(), updated.getUser().getId(),
                updated.getTitle(), updated.getBody());

        mockMvc.perform(put("/api/posts/999").contentType("application/json").content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePostWhenGivenValidID() throws Exception {
        doNothing().when(postService).deleteById(1);

        mockMvc.perform(delete("/api/posts/1")).andExpect(status().isNoContent());

        verify(postService, times(1)).deleteById(1);
    }

    @Test
    void shouldSearchPostsWhenGivenValidQuery() throws Exception {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        Post searchResult1 = new Post();
        searchResult1.setId(1);
        searchResult1.setUuid(UUID.randomUUID());
        searchResult1.setUser(user);
        searchResult1.setTitle("Spring Boot Tutorial");
        searchResult1.setBody("This is a tutorial about Spring Boot.");

        Post searchResult2 = new Post();
        searchResult2.setId(2);
        searchResult2.setUuid(UUID.randomUUID());
        searchResult2.setUser(user);
        searchResult2.setTitle("JPA Best Practices");
        searchResult2.setBody("Learn about JPA and Spring Boot integration.");

        List<Post> searchResults = List.of(searchResult1, searchResult2);

        when(findPosts.find("Spring", 10)).thenReturn(searchResults);

        String expectedResponse = """
                [
                    {
                        "id":%d,
                        "uuid":"%s",
                        "userId":%d,
                        "title":"%s",
                        "body":"%s"
                    },
                    {
                        "id":%d,
                        "uuid":"%s",
                        "userId":%d,
                        "title":"%s",
                        "body":"%s"
                    }
                ]
                """.formatted(searchResult1.getId(), searchResult1.getUuid(),
                searchResult1.getUser().getId(), searchResult1.getTitle(), searchResult1.getBody(),
                searchResult2.getId(), searchResult2.getUuid(), searchResult2.getUser().getId(),
                searchResult2.getTitle(), searchResult2.getBody());

        mockMvc.perform(get("/api/posts/search").param("q", "Spring").param("limit", "10"))
                .andExpect(status().isOk()).andExpect(content().json(expectedResponse));

        verify(findPosts, times(1)).find("Spring", 10);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public PostService postService() {
            return mock(PostService.class);
        }

        @Bean
        @Primary
        public FindPosts findPosts() {
            return mock(FindPosts.class);
        }

        @Bean
        @Primary
        public UpdatePost updatePost() {
            return mock(UpdatePost.class);
        }

        @Bean
        @Primary
        public SavePost savePost() {
            return mock(SavePost.class);
        }

        @Bean
        public PostMapper postMapper() {
            return new PostMapper();
        }
    }
}
