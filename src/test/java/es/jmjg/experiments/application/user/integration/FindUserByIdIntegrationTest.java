package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.user.FindUserById;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindUserByIdIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindUserById findUserById;

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
  void findById_WhenUserExists_ShouldReturnUser() {
    // Given
    User savedUser = userRepository.save(testUser);

    // When
    Optional<User> result = findUserById.findById(savedUser.getId());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    assertThat(result.get().getUuid()).isEqualTo(testUser.getUuid());
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findById_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserById.findById(999);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findById_WhenIdIsNull_ShouldThrowException() {
    // When & Then
    assertThatThrownBy(() -> findUserById.findById(null))
        .isInstanceOf(org.springframework.dao.InvalidDataAccessApiUsageException.class)
        .hasMessageContaining("The given id must not be null");
  }

  @Test
  void findById_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given
    User secondUser = UserFactory.createUser("Second User", "second@example.com", "seconduser");

    User savedFirstUser = userRepository.save(testUser);
    User savedSecondUser = userRepository.save(secondUser);

    // When
    Optional<User> firstResult = findUserById.findById(savedFirstUser.getId());
    Optional<User> secondResult = findUserById.findById(savedSecondUser.getId());

    // Then
    assertThat(firstResult).isPresent();
    assertThat(firstResult.get().getName()).isEqualTo("Test User");
    assertThat(firstResult.get().getId()).isEqualTo(savedFirstUser.getId());

    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo("Second User");
    assertThat(secondResult.get().getId()).isEqualTo(savedSecondUser.getId());
  }

  @Test
  void findById_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given
    User savedUser = userRepository.save(testUser);
    savedUser.setName("Updated Test User");
    savedUser.setEmail("updated@example.com");
    userRepository.save(savedUser);

    // When
    Optional<User> result = findUserById.findById(savedUser.getId());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Updated Test User");
    assertThat(result.get().getEmail()).isEqualTo("updated@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    assertThat(result.get().getUuid()).isEqualTo(testUser.getUuid());
  }

  @Test
  void findById_WhenIdIsZero_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserById.findById(0);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findById_WhenIdIsNegative_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserById.findById(-1);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findById_WhenUserIsDeleted_ShouldReturnEmpty() {
    // Given
    User savedUser = userRepository.save(testUser);
    Integer userId = savedUser.getId();

    // When
    Optional<User> resultBeforeDelete = findUserById.findById(userId);
    userRepository.deleteById(userId);
    Optional<User> resultAfterDelete = findUserById.findById(userId);

    // Then
    assertThat(resultBeforeDelete).isPresent();
    assertThat(resultAfterDelete).isEmpty();
  }

  @Test
  void findById_WhenMultipleUsersWithSameName_ShouldReturnCorrectUser() {
    // Given
    User secondUser = UserFactory.createUser("Test User", "test2@example.com", "testuser2");

    User savedFirstUser = userRepository.save(testUser);
    User savedSecondUser = userRepository.save(secondUser);

    // When
    Optional<User> firstResult = findUserById.findById(savedFirstUser.getId());
    Optional<User> secondResult = findUserById.findById(savedSecondUser.getId());

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
}
