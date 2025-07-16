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

import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SaveUserIntegrationTest extends TestContainersConfig {

  @Autowired
  private SaveUser saveUser;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
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
  void save_WhenUserIsValid_ShouldSaveAndReturnUser() {
    User userToSave = UserFactory.createUser("Test User", "test@example.com", "testuser");
    UUID originalUuid = userToSave.getUuid();

    User result = saveUser.save(userToSave);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getName()).isEqualTo("Test User");
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    assertThat(result.getUsername()).isEqualTo("testuser");
    assertThat(result.getUuid()).isEqualTo(originalUuid);

    Optional<User> dbUser = userRepository.findById(result.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getName()).isEqualTo("Test User");
    assertThat(dbUser.get().getEmail()).isEqualTo("test@example.com");
    assertThat(dbUser.get().getUsername()).isEqualTo("testuser");
    assertThat(dbUser.get().getUuid()).isEqualTo(originalUuid);
  }

  @Test
  void save_WhenUserHasValidData_ShouldSaveAndReturnUser() {
    User userToSave = UserFactory.createUser("Test User", "test@example.com", "testuser");

    User result = saveUser.save(userToSave);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getUuid()).isNotNull();
    assertThat(result.getName()).isEqualTo("Test User");
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    assertThat(result.getUsername()).isEqualTo("testuser");

    Optional<User> dbUser = userRepository.findById(result.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getUuid()).isNotNull();
  }

  @Test
  void save_WhenUserHasNoId_ShouldSaveAndReturnUserWithGeneratedId() {
    User userToSave = UserFactory.createUser("Test User", "test@example.com", "testuser");
    userToSave.setId(null);

    User result = saveUser.save(userToSave);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getName()).isEqualTo("Test User");
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    assertThat(result.getUsername()).isEqualTo("testuser");

    Optional<User> dbUser = userRepository.findById(result.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getName()).isEqualTo("Test User");
  }

  @Test
  void save_WhenMultipleUsers_ShouldSaveAllUsersWithDifferentIds() {
    User user1 = UserFactory.createUser("User 1", "user1@example.com", "user1");
    User user2 = UserFactory.createUser("User 2", "user2@example.com", "user2");

    User result1 = saveUser.save(user1);
    User result2 = saveUser.save(user2);

    assertThat(result1.getId()).isNotNull();
    assertThat(result2.getId()).isNotNull();
    assertThat(result1.getId()).isNotEqualTo(result2.getId());

    Optional<User> dbUser1 = userRepository.findById(result1.getId());
    Optional<User> dbUser2 = userRepository.findById(result2.getId());
    assertThat(dbUser1).isPresent();
    assertThat(dbUser2).isPresent();
    assertThat(dbUser1.get().getName()).isEqualTo("User 1");
    assertThat(dbUser2.get().getName()).isEqualTo("User 2");
  }

  @Test
  void save_WhenUserWithExistingUuid_ShouldThrowException() {
    User existingUser = userRepository.save(UserFactory.createBasicUser());
    User userToSave = UserFactory.createUser("New User", "new@example.com", "newuser");
    userToSave.setUuid(existingUser.getUuid());

    assertThatThrownBy(() -> saveUser.save(userToSave))
        .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
  }
}