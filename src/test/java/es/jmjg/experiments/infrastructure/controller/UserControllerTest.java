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
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import es.jmjg.experiments.application.user.DeleteUserByUuid;
import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.ControllerTestConfig;
import es.jmjg.experiments.shared.UserFactory;

@WebMvcTest(UserController.class)
@Import(ControllerTestConfig.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SaveUser saveUser;

  @Autowired
  private UpdateUser updateUser;

  @Autowired
  private FindUserByUuid findUserByUuid;

  @Autowired
  private FindUserByEmail findUserByEmail;

  @Autowired
  private FindUserByUsername findUserByUsername;

  @Autowired
  private FindAllUsers findAllUsers;

  @Autowired
  private DeleteUserByUuid deleteUserByUuid;

  private User testUser;
  private UUID testUuid;
  private Integer testId;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testId = 1;
    testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    testUser.setId(testId);
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void shouldFindAllUsers() throws Exception {
    // Given
    List<User> users = List.of(testUser);
    Page<User> userPage = new PageImpl<>(users, pageable, users.size());
    when(findAllUsers.findAll(any(Pageable.class))).thenReturn(userPage);

    String expectedJson = """
        {
            "content":[
                {
                    "uuid":"%s",
                    "name":"Test User",
                    "email":"test@example.com",
                    "username":"testuser"
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
        """.formatted(testUuid);

    // When & Then
    ResultActions resultActions = mockMvc
        .perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));

    JSONAssert.assertEquals(
        expectedJson, resultActions.andReturn().getResponse().getContentAsString(), false);
  }

  @Test
  void shouldFindUserWhenGivenValidUuid() throws Exception {
    // Given
    when(findUserByUuid.findByUuid(testUuid)).thenReturn(Optional.of(testUser));

    String expectedJson = """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser"
        }
        """.formatted(testUuid);

    // When & Then
    mockMvc
        .perform(get("/api/users/" + testUuid))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidUuid() throws Exception {
    // Given
    UUID invalidUuid = UUID.randomUUID();
    when(findUserByUuid.findByUuid(invalidUuid)).thenReturn(Optional.empty());

    // When & Then
    mockMvc.perform(get("/api/users/" + invalidUuid)).andExpect(status().isNotFound());
  }

  @Test
  void shouldFindUserWhenGivenValidEmail() throws Exception {
    // Given
    String email = "test@example.com";
    when(findUserByEmail.findByEmail(email)).thenReturn(Optional.of(testUser));

    String expectedJson = """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser"
        }
        """.formatted(testUuid);

    // When & Then
    mockMvc
        .perform(get("/api/users/search/email").param("email", email))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidEmail() throws Exception {
    // Given
    String invalidEmail = "nonexistent@example.com";
    when(findUserByEmail.findByEmail(invalidEmail)).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/api/users/search/email").param("email", invalidEmail))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldFindUserWhenGivenValidUsername() throws Exception {
    // Given
    String username = "testuser";
    when(findUserByUsername.findByUsername(username)).thenReturn(Optional.of(testUser));

    String expectedJson = """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser"
        }
        """.formatted(testUuid);

    // When & Then
    mockMvc
        .perform(get("/api/users/search/username").param("username", username))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidUsername() throws Exception {
    // Given
    String invalidUsername = "nonexistentuser";
    when(findUserByUsername.findByUsername(invalidUsername)).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/api/users/search/username").param("username", invalidUsername))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldCreateNewUserWhenGivenValidData() throws Exception {
    // Given
    User savedUser = UserFactory.createUser(testId, testUuid, "Test User", "test@example.com", "testuser");
    when(saveUser.save(any(User.class))).thenReturn(savedUser);

    String requestBody = """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser"
        }
        """.formatted(testUuid);

    String expectedResponse = """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser"
        }
        """.formatted(testUuid);

    // When & Then
    mockMvc
        .perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(content().json(expectedResponse));
  }

  @Test
  void shouldUpdateUserWhenGivenValidData() throws Exception {
    // Given
    User updatedUser = UserFactory.createUser(testId, testUuid, "Updated User", "updated@example.com", "updateduser");
    when(findUserByUuid.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
    when(updateUser.update(eq(testId), any(User.class))).thenReturn(updatedUser);

    String requestBody = """
        {
            "uuid":"%s",
            "name":"Updated User",
            "email":"updated@example.com",
            "username":"updateduser"
        }
        """.formatted(testUuid);

    String expectedResponse = """
        {
            "uuid":"%s",
            "name":"Updated User",
            "email":"updated@example.com",
            "username":"updateduser"
        }
        """.formatted(testUuid);

    // When & Then
    mockMvc
        .perform(put("/api/users/" + testUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedResponse));
  }

  @Test
  void shouldDeleteUserWhenGivenValidUuid() throws Exception {
    // Given
    doNothing().when(deleteUserByUuid).deleteByUuid(testUuid);

    // When & Then
    mockMvc
        .perform(delete("/api/users/" + testUuid))
        .andExpect(status().isNoContent());

    verify(deleteUserByUuid, times(1)).deleteByUuid(testUuid);
  }
}
