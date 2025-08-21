package es.jmjg.experiments.infrastructure.controller.post;

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
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import es.jmjg.experiments.application.post.DeletePostByUuid;
import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.application.post.FindPosts;
import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.UpdatePost;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.ControllerTestConfig;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;

@WebMvcTest(PostController.class)
@Import(ControllerTestConfig.class)
class PostControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private DeletePostByUuid deletePostById;

  @Autowired
  private FindPosts findPosts;

  @Autowired
  private UpdatePost updatePost;

  @Autowired
  private SavePost savePost;

  @Autowired
  private FindPostByUuid findPostByUuid;

  @Autowired
  private FindAllPosts findAllPosts;

  List<Post> posts;
  Page<Post> postsPage;

  @BeforeEach
  void setUp() {
    User user = UserFactory.createJohnDoeUser(1);

    Post post1 = PostFactory.createPost(user, UUID.randomUUID(), "Hello, World!", "This is my first post.");
    post1.setId(1);

    Post post2 = PostFactory.createPost(user, UUID.randomUUID(), "Second Post", "This is my second post.");
    post2.setId(2);

    posts = List.of(post1, post2);
    Pageable pageable = PageRequest.of(0, 20);
    postsPage = new PageImpl<>(posts, pageable, posts.size());
  }

  @Test
  void shouldFindAllPostsUsingWhen() throws Exception {
    String jsonResponse = """
        {
            "content":[
                {
                    "uuid":"%s",
                    "userId":"%s",
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "tags":[]
                },
                {
                    "uuid":"%s",
                    "userId":"%s",
                    "title":"Second Post",
                    "body":"This is my second post.",
                    "tags":[]
                }
            ],
            "pageNumber":0,
            "pageSize":20,
            "totalElements":2,
            "totalPages":1,
            "hasNext":false,
            "hasPrevious":false
        }
        """
        .formatted(posts.get(0).getUuid(), posts.get(0).getUser().getUuid(), posts.get(1).getUuid(),
            posts.get(1).getUser().getUuid());

    when(findAllPosts.findAll(any(Pageable.class))).thenReturn(postsPage);

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    JSONAssert.assertEquals(
        jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
  }

  @Test
  void shouldFindAllPostsUsingDoReturn() throws Exception {
    String jsonResponse = """
        {
            "content":[
                {
                    "uuid":"%s",
                    "userId":"%s",
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "tags":[]
                },
                {
                    "uuid":"%s",
                    "userId":"%s",
                    "title":"Second Post",
                    "body":"This is my second post.",
                    "tags":[]
                }
            ],
            "pageNumber":0,
            "pageSize":20,
            "totalElements":2,
            "totalPages":1,
            "hasNext":false,
            "hasPrevious":false
        }
        """
        .formatted(posts.get(0).getUuid(), posts.get(0).getUser().getUuid(), posts.get(1).getUuid(),
            posts.get(1).getUser().getUuid());

    doReturn(postsPage).when(findAllPosts).findAll(any(Pageable.class));

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    JSONAssert.assertEquals(
        jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
  }

  @Test
  void shouldFindAllPostsWithPagination() throws Exception {
    String jsonResponse = """
        {
            "content":[
                {
                    "uuid":"%s",
                    "userId":"%s",
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "tags":[]
                }
            ],
            "pageNumber":0,
            "pageSize":1,
            "totalElements":2,
            "totalPages":2,
            "hasNext":true,
            "hasPrevious":false
        }
        """
        .formatted(posts.get(0).getUuid(), posts.get(0).getUser().getUuid());

    Pageable pageable = PageRequest.of(0, 1);
    Page<Post> singlePostPage = new PageImpl<>(List.of(posts.get(0)), pageable, 2);
    when(findAllPosts.findAll(any(Pageable.class))).thenReturn(singlePostPage);

    ResultActions resultActions = mockMvc
        .perform(get("/api/posts?page=0&size=1"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));

    JSONAssert.assertEquals(
        jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
  }

  @Test
  void shouldFindPostWhenGivenValidUuid() throws Exception {
    User user = UserFactory.createTestUserWithId1();

    UUID uuid = UUID.randomUUID();
    Post post = PostFactory.createPost(user, uuid, "Test Title", "Test Body");
    post.setId(1);

    when(findPostByUuid.findByUuid(uuid)).thenReturn(Optional.of(post));
    String json = """
        {
            "uuid":"%s",
            "userId":"%s",
            "title":"%s",
            "body":"%s",
            "tags":[]
        }
        """
        .formatted(
            post.getUuid(),
            post.getUser().getUuid(),
            post.getTitle(),
            post.getBody());

    mockMvc
        .perform(get("/api/posts/" + uuid))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }

  @Test
  void shouldNotFindPostWhenGivenInvalidUuid() throws Exception {
    UUID uuid = UUID.randomUUID();
    when(findPostByUuid.findByUuid(uuid)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/posts/" + uuid)).andExpect(status().isNotFound());
  }

  @Test
  void shouldCreateNewPostWhenGivenValidID() throws Exception {
    User user = UserFactory.createTestUserWithId1();

    Post post = PostFactory.createPost(user, UUID.randomUUID(), "This is my brand new post", "TEST BODY");
    post.setId(3);

    when(savePost.save(any(Post.class), eq(user.getUuid()), any())).thenReturn(post);

    // Request body should be a PostDto (without id, since it's a new post)
    String requestBody = """
        {
            "uuid":"%s",
            "userId":"%s",
            "title":"%s",
            "body":"%s"
        }
        """
        .formatted(post.getUuid(), user.getUuid(), post.getTitle(), post.getBody());

    // Expected response should not include the id
    String expectedResponse = """
        {
            "uuid":"%s",
            "userId":"%s",
            "title":"%s",
            "body":"%s",
            "tags":[]
        }
        """
        .formatted(
            post.getUuid(),
            post.getUser().getUuid(),
            post.getTitle(),
            post.getBody());

    mockMvc
        .perform(post("/api/posts").contentType("application/json").content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/posts/" + post.getUuid().toString()))
        .andExpect(content().json(expectedResponse));
  }

  @Test
  void shouldUpdatePostWhenGivenValidPost() throws Exception {
    User user = UserFactory.createTestUserWithId1();

    Post updated = PostFactory.createPost(
        user, UUID.randomUUID(), "This is my brand new post", "UPDATED BODY");
    updated.setId(1);

    when(updatePost.update(eq(TestDataSamples.POST_2_UUID), any(Post.class), any())).thenReturn(updated);
    String requestBody = """
        {
            "uuid":"%s",
            "userId":"%s",
            "title":"%s",
            "body":"%s"
        }
        """
        .formatted(
            updated.getUuid(),
            updated.getUser().getUuid(),
            updated.getTitle(),
            updated.getBody());

    mockMvc
        .perform(put("/api/posts/" + TestDataSamples.POST_2_UUID).contentType("application/json").content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(requestBody + ",\"tags\":[]"));
  }

  @Test
  void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPostUUID() throws Exception {
    User user = new User();
    user.setId(1);
    user.setUuid(UUID.randomUUID());
    user.setName("Test User");
    user.setEmail("test@example.com");
    user.setUsername("testuser");
    user.setPassword("encodedPassword123");

    Post updated = new Post();
    updated.setId(50);
    updated.setUuid(UUID.randomUUID());
    updated.setUser(user);
    updated.setTitle("This is my brand new post");
    updated.setBody("UPDATED BODY");

    UUID nonExistentUuid = UUID.randomUUID();
    when(updatePost.update(eq(nonExistentUuid), any(Post.class), any()))
        .thenThrow(new PostNotFound("Post not found with uuid: " + nonExistentUuid));
    String json = """
        {
            "uuid":"%s",
            "userId":"%s",
            "title":"%s",
            "body":"%s"
        }
        """
        .formatted(
            updated.getUuid(),
            updated.getUser().getUuid(),
            updated.getTitle(),
            updated.getBody());

    mockMvc
        .perform(put("/api/posts/" + nonExistentUuid).contentType("application/json").content(json))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldDeletePostWhenGivenValidUUID() throws Exception {
    doNothing().when(deletePostById).deleteByUuid(TestDataSamples.POST_2_UUID);

    mockMvc.perform(delete("/api/posts/" + TestDataSamples.POST_2_UUID)).andExpect(status().isNoContent());

    verify(deletePostById, times(1)).deleteByUuid(TestDataSamples.POST_2_UUID);
  }

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

    String expectedJson = """
        [
            {
                "uuid":"%s",
                "userId":"%s",
                "title":"Spring Boot Tutorial",
                "body":"Learn Spring Boot",
                "tags":[]
            },
            {
                "uuid":"%s",
                "userId":"%s",
                "title":"JPA Best Practices",
                "body":"Learn JPA",
                "tags":[]
            }
        ]
        """
        .formatted(searchResult1.getUuid(), searchResult1.getUser().getUuid(),
            searchResult2.getUuid(),
            searchResult2.getUser().getUuid());

    mockMvc
        .perform(get("/api/posts/search?q=Spring&limit=10"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }
}
