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

import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindUserByUuidIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindUserByUuid findUserByUuid;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser;
  private UUID testUuid;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    testUuid = UUID.randomUUID();
    testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
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
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // Given
    User savedUser = userRepository.save(testUser);

    // When
    Optional<User> result = findUserByUuid.findByUuid(testUuid);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    assertThat(result.get().getUuid()).isEqualTo(testUuid);
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByUuid_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByUuid.findByUuid(UUID.randomUUID());

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByUuid.findByUuid(null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuid_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given
    UUID secondUuid = UUID.randomUUID();
    User secondUser =
        UserFactory.createUser(secondUuid, "Second User", "second@example.com", "seconduser");

    User savedFirstUser = userRepository.save(testUser);
    User savedSecondUser = userRepository.save(secondUser);

    // When
    Optional<User> firstResult = findUserByUuid.findByUuid(testUuid);
    Optional<User> secondResult = findUserByUuid.findByUuid(secondUuid);

    // Then
    assertThat(firstResult).isPresent();
    assertThat(firstResult.get().getName()).isEqualTo("Test User");
    assertThat(firstResult.get().getId()).isEqualTo(savedFirstUser.getId());

    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo("Second User");
    assertThat(secondResult.get().getId()).isEqualTo(savedSecondUser.getId());
  }

  @Test
  void findByUuid_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given
    User savedUser = userRepository.save(testUser);
    savedUser.setName("Updated Test User");
    savedUser.setEmail("updated@example.com");
    userRepository.save(savedUser);

    // When
    Optional<User> result = findUserByUuid.findByUuid(testUuid);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Updated Test User");
    assertThat(result.get().getEmail()).isEqualTo("updated@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    assertThat(result.get().getUuid()).isEqualTo(testUuid);
  }
}
