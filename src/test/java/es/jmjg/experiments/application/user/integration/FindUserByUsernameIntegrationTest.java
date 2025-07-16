package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindUserByUsernameIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindUserByUsername findUserByUsername;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser;
  private String testUsername;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    testUsername = "testuser";
    testUser =
        UserFactory.createUser(UUID.randomUUID(), "Test User", "test@example.com", testUsername);
  }

  @Test
  void shouldUseTestProfile() {
    String[] activeProfiles = environment.getActiveProfiles();
    assertThat(activeProfiles).contains("test");
  }

  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

  @Test
  void findByUsername_WhenUserExists_ShouldReturnUser() {
    // Given
    User savedUser = userRepository.save(testUser);

    // When
    Optional<User> result = findUserByUsername.findByUsername(testUsername);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo(testUsername);
    assertThat(result.get().getUuid()).isEqualTo(testUser.getUuid());
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByUsername.findByUsername("nonexistentuser");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameIsNull_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByUsername.findByUsername(null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given
    String secondUsername = "seconduser";
    User secondUser =
        UserFactory.createUser(UUID.randomUUID(), "Second User", "second@example.com",
            secondUsername);

    User savedFirstUser = userRepository.save(testUser);
    User savedSecondUser = userRepository.save(secondUser);

    // When
    Optional<User> firstResult = findUserByUsername.findByUsername(testUsername);
    Optional<User> secondResult = findUserByUsername.findByUsername(secondUsername);

    // Then
    assertThat(firstResult).isPresent();
    assertThat(firstResult.get().getName()).isEqualTo("Test User");
    assertThat(firstResult.get().getId()).isEqualTo(savedFirstUser.getId());

    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo("Second User");
    assertThat(secondResult.get().getId()).isEqualTo(savedSecondUser.getId());
  }

  @Test
  void findByUsername_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given
    User savedUser = userRepository.save(testUser);
    savedUser.setName("Updated Test User");
    savedUser.setEmail("updated@example.com");
    userRepository.save(savedUser);

    // When
    Optional<User> result = findUserByUsername.findByUsername(testUsername);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Updated Test User");
    assertThat(result.get().getEmail()).isEqualTo("updated@example.com");
    assertThat(result.get().getUsername()).isEqualTo(testUsername);
    assertThat(result.get().getUuid()).isEqualTo(testUser.getUuid());
  }

  @Test
  void findByUsername_WhenUsernameIsEmpty_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByUsername.findByUsername("");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameIsBlank_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByUsername.findByUsername("   ");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameCaseSensitive_ShouldReturnCorrectUser() {
    // Given
    userRepository.save(testUser);

    // When
    Optional<User> result = findUserByUsername.findByUsername("TESTUSER");

    // Then
    assertThat(result).isEmpty(); // Username search is case-sensitive
  }

  @Test
  void findByUsername_WhenUsernameWithSpaces_ShouldReturnEmpty() {
    // Given
    userRepository.save(testUser);

    // When
    Optional<User> result = findUserByUsername.findByUsername(" testuser ");

    // Then
    assertThat(result).isEmpty();
  }
}
