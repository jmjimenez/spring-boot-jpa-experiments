package es.jmjg.experiments.infrastructure.controller;

import es.jmjg.experiments.application.PostService;
import es.jmjg.experiments.domain.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    PostService postService;

    @InjectMocks
    PostController postController;

    MockMvc mockMvc;

    List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        posts = List.of(
            new Post(1,1,"Hello, World!", "This is my first post."),
            new Post(2,1,"Second Post", "This is my second post.")
        );
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

        ResultActions resultActions = mockMvc.perform(get("/api/posts"))
            .andExpect(status().isOk())
            .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
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

        Mockito.doReturn(posts).when(postService).findAll();

        ResultActions resultActions = mockMvc.perform(get("/api/posts"))
            .andExpect(status().isOk())
            .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        Post post = new Post(1,1,"Test Title", "Test Body");
        when(postService.findById(1)).thenReturn(Optional.of(post));
        String json = """
            {
                "id":%d,
                "userId":%d,
                "title":"%s",
                "body":"%s"
            }
            """.formatted(post.getId(), post.getUserId(), post.getTitle(), post.getBody());

        mockMvc.perform(get("/api/posts/1"))
            .andExpect(status().isOk())
            .andExpect(content().json(json));
    }

    @Test
    void shouldNotFindPostWhenGivenInvalidId() throws Exception {
        when(postService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/posts/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPostWhenGivenValidID() throws Exception {
        Post post = new Post(3,1,"This is my brand new post", "TEST BODY");
        when(postService.save(any(Post.class))).thenReturn(post);
        String json = """
            {
                "id":%d,
                "userId":%d,
                "title":"%s",
                "body":"%s"
            }
            """.formatted(post.getId(), post.getUserId(), post.getTitle(), post.getBody());

        ResultActions action = mockMvc.perform(post("/api/posts")
            .contentType("application/json")
            .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    void shouldUpdatePostWhenGivenValidPost() throws Exception {
        Post updated = new Post(1,1,"This is my brand new post", "UPDATED BODY");
        when(postService.update(eq(1), any(Post.class))).thenReturn(updated);
        String requestBody = """
            {
                "id":%d,
                "userId":%d,
                "title":"%s",
                "body":"%s"
            }
            """.formatted(updated.getId(), updated.getUserId(), updated.getTitle(), updated.getBody());

        mockMvc.perform(put("/api/posts/1")
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content().json(requestBody));
    }

    @Test
    void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPostID() throws Exception {
        Post updated = new Post(50,1,"This is my brand new post", "UPDATED BODY");
        when(postService.update(eq(999), any(Post.class))).thenThrow(new RuntimeException("Post not found with id: 999"));
        String json = """
            {
                "id":%d,
                "userId":%d,
                "title":"%s",
                "body":"%s"
            }
            """.formatted(updated.getId(), updated.getUserId(), updated.getTitle(), updated.getBody());

        mockMvc.perform(put("/api/posts/999")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePostWhenGivenValidID() throws Exception {
        doNothing().when(postService).deleteById(1);

        mockMvc.perform(delete("/api/posts/1"))
            .andExpect(status().isNoContent());

        verify(postService, times(1)).deleteById(1);
    }
}