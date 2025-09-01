package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.Mockito.reset;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import es.jmjg.experiments.application.user.DeleteUser;
import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.ControllerTestConfig;
import es.jmjg.experiments.shared.UserFactory;

@WebMvcTest(UserController.class)
@Import(ControllerTestConfig.class)
abstract class BaseUserControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected SaveUser saveUser;

  @Autowired
  protected UpdateUser updateUser;

  @Autowired
  protected FindUserByUuid findUserByUuid;

  @Autowired
  protected FindUserByEmail findUserByEmail;

  @Autowired
  protected FindUserByUsername findUserByUsername;

  @Autowired
  protected FindAllUsers findAllUsers;

  @Autowired
  protected DeleteUser deleteUser;

  protected User testUser;
  protected UUID testUuid;
  protected Integer testId;
  protected User adminUser;
  protected Pageable pageable;
  protected List<Post> testPosts;
  protected List<Tag> testTags;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testId = 1;
    testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    testUser.setId(testId);
    adminUser = UserFactory.createUser(UUID.randomUUID(), "Admin User", "admin@example.com", "admin");


    // Create test posts
    testPosts = new ArrayList<>();
    Post post1 = new Post();
    post1.setUuid(UUID.randomUUID());
    testPosts.add(post1);
    Post post2 = new Post();
    post2.setUuid(UUID.randomUUID());
    testPosts.add(post2);
    testUser.setPosts(testPosts);

    // Create test tags
    testTags = new ArrayList<>();
    Tag tag1 = new Tag();
    tag1.setName("technology");
    testTags.add(tag1);
    Tag tag2 = new Tag();
    tag2.setName("java");
    testTags.add(tag2);
    testUser.setTags(testTags);

    pageable = PageRequest.of(0, 10);

    // Reset all mocks to ensure clean state between tests
    reset(deleteUser, updateUser);
  }

  protected String createFindAllUsersJsonResponse() {
    return """
        {
            "content":[
                {
                    "uuid":"%s",
                    "name":"Test User",
                    "email":"test@example.com",
                    "username":"testuser",
                    "posts":["%s","%s"],
                    "tags":["technology","java"]
                }
            ],
            "pageable":{
                "sort":{
                    "empty":true,
                    "sorted":false,
                    "unsorted":true
                },
                "offset":0,
                "pageNumber":0,
                "pageSize":10,
                "paged":true,
                "unpaged":false
            },
            "last":true,
            "totalElements":1,
            "totalPages":1,
            "size":10,
            "number":0,
            "sort":{
                "empty":true,
                "sorted":false,
                "unsorted":true
            },
            "first":true,
            "numberOfElements":1,
            "empty":false
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  protected String createFindUserByUuidJsonResponse() {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  protected String createFindUserByEmailJsonResponse() {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  protected String createFindUserByUsernameJsonResponse() {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  protected String createSaveUserRequestJson() {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "password":"testpassword123"
        }
        """.formatted(testUuid);
  }

  protected String createSaveUserResponseJson() {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser"
        }
        """.formatted(testUuid);
  }

  protected String createUpdateUserRequestJson() {
    return """
        {
            "uuid":"%s",
            "name":"Updated User",
            "email":"updated@example.com",
            "username":"updateduser",
            "password":"updatedpassword123"
        }
        """.formatted(testUuid);
  }

  protected String createUpdateUserResponseJson() {
    return """
        {
            "uuid":"%s",
            "name":"Updated User",
            "email":"updated@example.com",
            "username":"updateduser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  protected void verifyJsonResponse(ResultActions resultActions, String expectedJson) throws Exception {
    JSONAssert.assertEquals(
        expectedJson, resultActions.andReturn().getResponse().getContentAsString(), false);
  }
}
