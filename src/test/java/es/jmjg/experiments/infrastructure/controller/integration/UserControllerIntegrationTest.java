package es.jmjg.experiments.infrastructure.controller.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.controller.dto.UserRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.UserResponseDto;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.UserFactory;

class UserControllerIntegrationTest extends BaseControllerIntegration {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private UserRepository userRepository;

  private User testUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
  }

  @Test
  void shouldGetAllUsers() {
    ResponseEntity<Object> response = restTemplate.getForEntity("/api/users", Object.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Object responseBody = response.getBody();
    assertThat(responseBody).isNotNull();
  }

  @Test
  void shouldFindUserByUuid() {
    User savedUser = userRepository.save(testUser);
    UUID userUuid = savedUser.getUuid();

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity("/api/users/" + userUuid,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getUuid()).isEqualTo(userUuid);
      assertThat(u.getName()).isEqualTo("Test User");
      assertThat(u.getEmail()).isEqualTo("test@example.com");
      assertThat(u.getUsername()).isEqualTo("testuser");
    });
  }

  @Test
  void shouldFindUserByEmail() {
    User savedUser = userRepository.save(testUser);
    String userEmail = savedUser.getEmail();

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity("/api/users/search/email?email=" + userEmail,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getEmail()).isEqualTo(userEmail);
      assertThat(u.getName()).isEqualTo("Test User");
      assertThat(u.getUsername()).isEqualTo("testuser");
    });
  }

  @Test
  void shouldFindUserByUsername() {
    User savedUser = userRepository.save(testUser);
    String username = savedUser.getUsername();

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/username?username=" + username,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getUsername()).isEqualTo(username);
      assertThat(u.getName()).isEqualTo("Test User");
      assertThat(u.getEmail()).isEqualTo("test@example.com");
    });
  }

  @Test
  @DirtiesContext
  void shouldCreateNewUserWhenUserIsValid() {
    UserRequestDto userDto = new UserRequestDto(UUID.randomUUID(), "New User", "new@example.com", "newuser");

    ResponseEntity<UserResponseDto> response = restTemplate.exchange(
        "/api/users", HttpMethod.POST, new HttpEntity<>(userDto), UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    UserResponseDto user = response.getBody();
    assertThat(user).isNotNull().satisfies(u -> {
      assertThat(u.getName()).isEqualTo("New User");
      assertThat(u.getEmail()).isEqualTo("new@example.com");
      assertThat(u.getUsername()).isEqualTo("newuser");
      assertThat(u.getUuid()).isNotNull();
    });
  }

  @Test
  @DirtiesContext
  void shouldUpdateExistingUser() {
    User savedUser = userRepository.save(testUser);
    UUID userUuid = savedUser.getUuid();

    UserRequestDto updateDto = new UserRequestDto(userUuid, "Updated User", "updated@example.com", "updateduser");

    ResponseEntity<UserResponseDto> response = restTemplate.exchange(
        "/api/users/" + userUuid, HttpMethod.PUT, new HttpEntity<>(updateDto),
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto updatedUser = response.getBody();
    assertThat(updatedUser).isNotNull().satisfies(u -> {
      assertThat(u.getUuid()).isEqualTo(userUuid);
      assertThat(u.getName()).isEqualTo("Updated User");
      assertThat(u.getEmail()).isEqualTo("updated@example.com");
      assertThat(u.getUsername()).isEqualTo("updateduser");
    });
  }

  @Test
  void shouldDeleteUserByUuid() {
    User savedUser = userRepository.save(testUser);
    UUID userUuid = savedUser.getUuid();

    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + userUuid, HttpMethod.DELETE, null, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // Verify user is deleted
    assertThat(userRepository.findByUuid(userUuid)).isEmpty();
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity("/api/users/" + nonExistentUuid,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByUuidDoesNotExist() {
    UUID nonExistentUuid = UUID.randomUUID();

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity("/api/users/" + nonExistentUuid,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByEmailDoesNotExist() {
    String nonExistentEmail = "nonexistent@example.com";

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/email?email=" + nonExistentEmail,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldReturnNotFoundWhenUserByUsernameDoesNotExist() {
    String nonExistentUsername = "nonexistentuser";

    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity(
        "/api/users/search/username?username=" + nonExistentUsername,
        UserResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
