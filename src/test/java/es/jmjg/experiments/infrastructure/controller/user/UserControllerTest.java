package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import es.jmjg.experiments.application.user.dto.FindAllUsersDto;
import es.jmjg.experiments.application.user.dto.FindUserByEmailDto;
import es.jmjg.experiments.application.user.dto.FindUserByUsernameDto;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.domain.entity.User;

class UserControllerTest extends BaseUserControllerTest {

  @Test
  void shouldFindAllUsers() throws Exception {
    // Given
    List<User> users = List.of(testUser);
    Page<User> userPage = new PageImpl<>(users, pageable, users.size());
    when(findAllUsers.findAll(any(FindAllUsersDto.class))).thenReturn(userPage);

    String expectedJson = createFindAllUsersJsonResponse();

    // When & Then
    mockMvc
        .perform(get("/api/users").header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldFindUserWhenGivenValidUuid() throws Exception {
    // Given
    when(findUserByUuid.findByUuid(any(FindUserByUuidDto.class))).thenReturn(Optional.of(testUser));

    String expectedJson = createFindUserByUuidJsonResponse();

    // When & Then
    mockMvc
        .perform(get("/api/users/" + testUuid).header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidUuid() throws Exception {
    // Given
    java.util.UUID invalidUuid = java.util.UUID.randomUUID();
    when(findUserByUuid.findByUuid(any(FindUserByUuidDto.class))).thenReturn(Optional.empty());

    // When & Then
    mockMvc.perform(get("/api/users/" + invalidUuid)
        .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldFindUserWhenGivenValidEmail() throws Exception {
    // Given
    String email = "test@example.com";
    when(findUserByEmail.findByEmail(any(FindUserByEmailDto.class))).thenReturn(Optional.of(testUser));

    String expectedJson = createFindUserByEmailJsonResponse();

    // When & Then
    mockMvc
        .perform(get("/api/users/search/email").param("email", email)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidEmail() throws Exception {
    // Given
    String invalidEmail = "nonexistent@example.com";
    when(findUserByEmail.findByEmail(any(FindUserByEmailDto.class))).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/api/users/search/email").param("email", invalidEmail)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldFindUserWhenGivenValidUsername() throws Exception {
    // Given
    String username = "testuser";
    when(findUserByUsername.findByUsername(any(FindUserByUsernameDto.class))).thenReturn(Optional.of(testUser));

    String expectedJson = createFindUserByUsernameJsonResponse();

    // When & Then
    mockMvc
        .perform(get("/api/users/search/username").param("username", username)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldNotFindUserWhenGivenInvalidUsername() throws Exception {
    // Given
    String invalidUsername = "nonexistentuser";
    when(findUserByUsername.findByUsername(any(FindUserByUsernameDto.class))).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/api/users/search/username").param("username", invalidUsername)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldCreateNewUserWhenGivenValidData() throws Exception {
    // Given
    User savedUser = es.jmjg.experiments.shared.UserFactory.createUser(testId, testUuid, "Test User",
        "test@example.com", "testuser");
    savedUser.setPosts(testPosts);
    savedUser.setTags(testTags);
    when(saveUser.save(any(SaveUserDto.class))).thenReturn(savedUser);

    String requestBody = createSaveUserRequestJson();
    String expectedResponse = createSaveUserResponseJson();

    // When & Then
    mockMvc
        .perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header("Authorization", "Bearer " + testUser.getUsername()))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/users/" + testUuid.toString()))
        .andExpect(content().json(expectedResponse));
  }

}
