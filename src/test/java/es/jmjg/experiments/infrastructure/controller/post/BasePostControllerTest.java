package es.jmjg.experiments.infrastructure.controller.post;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

import es.jmjg.experiments.application.post.DeletePost;
import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.application.post.FindPosts;
import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.UpdatePost;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.ControllerTestConfig;
import es.jmjg.experiments.infrastructure.config.security.JwtSecurityConfig;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@WebMvcTest(PostController.class)
@Import({ ControllerTestConfig.class, JwtSecurityConfig.class })
abstract class BasePostControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected DeletePost deletePost;

  @Autowired
  protected FindPosts findPosts;

  @Autowired
  protected UpdatePost updatePost;

  @Autowired
  protected SavePost savePost;

  @Autowired
  protected FindPostByUuid findPostByUuid;

  @Autowired
  protected FindAllPosts findAllPosts;

  protected List<Post> posts;
  protected Page<Post> postsPage;
  protected User testUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createJohnDoeUser(1);

    Post post1 = PostFactory.createPost(testUser, UUID.randomUUID(), "Hello, World!", "This is my first post.");
    post1.setId(1);

    Post post2 = PostFactory.createPost(testUser, UUID.randomUUID(), "Second Post", "This is my second post.");
    post2.setId(2);

    posts = List.of(post1, post2);
    Pageable pageable = PageRequest.of(0, 20);
    postsPage = new PageImpl<>(posts, pageable, posts.size());
  }

  protected String createFindAllPostsJsonResponse() {
    return """
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
  }

  protected String createFindAllPostsWithPaginationJsonResponse() {
    return """
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
  }

  protected String createFindPostByUuidJsonResponse(Post post) {
    return """
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
  }

  protected String createCreatePostRequestJson(Post post) {
    return """
        {
            "uuid":"%s",
            "title":"%s",
            "body":"%s"
        }
        """
        .formatted(post.getUuid(), post.getTitle(), post.getBody());
  }

  protected String createCreatePostResponseJson(Post post) {
    return """
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
  }

  protected String createUpdatePostRequestJson(Post post) {
    return """
        {
            "uuid":"%s",
            "title":"%s",
            "body":"%s"
        }
        """
        .formatted(
            post.getUuid(),
            post.getTitle(),
            post.getBody());
  }

  protected String createSearchPostsJsonResponse(List<Post> searchResults) {
    return """
        [
            {
                "uuid":"%s",
                "title":"%s",
                "body":"%s",
                "tags":[]
            },
            {
                "uuid":"%s",
                "title":"%s",
                "body":"%s",
                "tags":[]
            }
        ]
        """
        .formatted(
            searchResults.get(0).getUuid(),
            searchResults.get(0).getTitle(),
            searchResults.get(0).getBody(),
            searchResults.get(1).getUuid(),
            searchResults.get(1).getTitle(),
            searchResults.get(1).getBody());
  }

  protected void verifyJsonResponse(ResultActions resultActions, String expectedJson) throws Exception {
    JSONAssert.assertEquals(
        expectedJson, resultActions.andReturn().getResponse().getContentAsString(), false);
  }
}
