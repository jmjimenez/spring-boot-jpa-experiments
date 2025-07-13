package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindUserByEmailIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindUserByEmail findUserByEmail;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
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
  void findByEmail_WhenUserExists_ShouldReturnUser() {
    // Given
    User savedUser = userRepository.save(testUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail(savedUser.getEmail());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    assertThat(result.get().getUuid()).isEqualTo(testUser.getUuid());
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByEmail.findByEmail("nonexistent@example.com");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailIsNull_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByEmail.findByEmail(null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given
    User secondUser = UserFactory.createUser("Second User", "second@example.com", "seconduser");

    User savedFirstUser = userRepository.save(testUser);
    User savedSecondUser = userRepository.save(secondUser);

    // When
    Optional<User> firstResult = findUserByEmail.findByEmail(savedFirstUser.getEmail());
    Optional<User> secondResult = findUserByEmail.findByEmail(savedSecondUser.getEmail());

    // Then
    assertThat(firstResult).isPresent();
    assertThat(firstResult.get().getName()).isEqualTo("Test User");
    assertThat(firstResult.get().getEmail()).isEqualTo("test@example.com");
    assertThat(firstResult.get().getId()).isEqualTo(savedFirstUser.getId());

    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo("Second User");
    assertThat(secondResult.get().getEmail()).isEqualTo("second@example.com");
    assertThat(secondResult.get().getId()).isEqualTo(savedSecondUser.getId());
  }

  @Test
  void findByEmail_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given
    User savedUser = userRepository.save(testUser);
    savedUser.setName("Updated Test User");
    savedUser.setEmail("updated@example.com");
    userRepository.save(savedUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail("updated@example.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Updated Test User");
    assertThat(result.get().getEmail()).isEqualTo("updated@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    assertThat(result.get().getUuid()).isEqualTo(testUser.getUuid());
  }

  @Test
  void findByEmail_WhenEmailIsEmpty_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByEmail.findByEmail("");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailIsBlank_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByEmail.findByEmail("   ");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenUserIsDeleted_ShouldReturnEmpty() {
    // Given
    User savedUser = userRepository.save(testUser);
    String userEmail = savedUser.getEmail();

    // When
    Optional<User> resultBeforeDelete = findUserByEmail.findByEmail(userEmail);
    userRepository.deleteById(savedUser.getId());
    Optional<User> resultAfterDelete = findUserByEmail.findByEmail(userEmail);

    // Then
    assertThat(resultBeforeDelete).isPresent();
    assertThat(resultAfterDelete).isEmpty();
  }

  @Test
  void findByEmail_WhenMultipleUsersWithSameName_ShouldReturnCorrectUser() {
    // Given
    User secondUser = UserFactory.createUser("Test User", "test2@example.com", "testuser2");

    User savedFirstUser = userRepository.save(testUser);
    User savedSecondUser = userRepository.save(secondUser);

    // When
    Optional<User> firstResult = findUserByEmail.findByEmail(savedFirstUser.getEmail());
    Optional<User> secondResult = findUserByEmail.findByEmail(savedSecondUser.getEmail());

    // Then
    assertThat(firstResult).isPresent();
    assertThat(firstResult.get().getName()).isEqualTo("Test User");
    assertThat(firstResult.get().getEmail()).isEqualTo("test@example.com");
    assertThat(firstResult.get().getId()).isEqualTo(savedFirstUser.getId());

    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo("Test User");
    assertThat(secondResult.get().getEmail()).isEqualTo("test2@example.com");
    assertThat(secondResult.get().getId()).isEqualTo(savedSecondUser.getId());
  }

  @Test
  void findByEmail_WhenEmailHasDifferentCase_ShouldReturnEmpty() {
    // Given
    userRepository.save(testUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail("TEST@EXAMPLE.COM");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailHasSpecialCharacters_ShouldReturnUser() {
    // Given
    User specialUser =
        UserFactory.createUser("Special User", "test+tag@example.com", "specialuser");
    User savedUser = userRepository.save(specialUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail("test+tag@example.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Special User");
    assertThat(result.get().getEmail()).isEqualTo("test+tag@example.com");
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByEmail_WhenEmailHasSubdomain_ShouldReturnUser() {
    // Given
    User subdomainUser =
        UserFactory.createUser("Subdomain User", "user@subdomain.example.com", "subdomainuser");
    User savedUser = userRepository.save(subdomainUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail("user@subdomain.example.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Subdomain User");
    assertThat(result.get().getEmail()).isEqualTo("user@subdomain.example.com");
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }
}
