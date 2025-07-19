package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SaveUserIntegrationTest extends TestContainersConfig {

  @Autowired
  private SaveUser saveUser;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

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
    User userToSave = UserFactory.createUser("Test User 01", "test01@example.com", "testuser01");
    UUID originalUuid = userToSave.getUuid();

    User result = saveUser.save(userToSave);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getName()).isEqualTo(userToSave.getName());
    assertThat(result.getEmail()).isEqualTo(userToSave.getEmail());
    assertThat(result.getUsername()).isEqualTo(userToSave.getUsername());
    assertThat(result.getUuid()).isEqualTo(originalUuid);

    Optional<User> dbUser = userRepository.findById(result.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getName()).isEqualTo(userToSave.getName());
    assertThat(dbUser.get().getEmail()).isEqualTo(userToSave.getEmail());
    assertThat(dbUser.get().getUsername()).isEqualTo(userToSave.getUsername());
    assertThat(dbUser.get().getUuid()).isEqualTo(originalUuid);
  }

  @Test
  void save_WhenUserHasValidData_ShouldSaveAndReturnUser() {
    User userToSave = UserFactory.createUser("Test User 02", "test02@example.com", "testuser02");

    User result = saveUser.save(userToSave);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getUuid()).isNotNull();
    assertThat(result.getName()).isEqualTo(userToSave.getName());
    assertThat(result.getEmail()).isEqualTo(userToSave.getEmail());
    assertThat(result.getUsername()).isEqualTo(userToSave.getUsername());

    Optional<User> dbUser = userRepository.findById(result.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getUuid()).isNotNull();
  }

  @Test
  void save_WhenUserHasNoId_ShouldSaveAndReturnUserWithGeneratedId() {
    User userToSave = UserFactory.createUser("Test User 03", "test03@example.com", "testuser03");
    userToSave.setId(null);

    User result = saveUser.save(userToSave);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getName()).isEqualTo(userToSave.getName());
    assertThat(result.getEmail()).isEqualTo(userToSave.getEmail());
    assertThat(result.getUsername()).isEqualTo(userToSave.getUsername());

    Optional<User> dbUser = userRepository.findById(result.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getName()).isEqualTo(userToSave.getName());
  }

  @Test
  void save_WhenMultipleUsers_ShouldSaveAllUsersWithDifferentIds() {
    User user1 = UserFactory.createUser("User 04", "user04@example.com", "user04");
    User user2 = UserFactory.createUser("User 05", "user05@example.com", "user05");

    User result1 = saveUser.save(user1);
    User result2 = saveUser.save(user2);

    assertThat(result1.getId()).isNotNull();
    assertThat(result2.getId()).isNotNull();
    assertThat(result1.getId()).isNotEqualTo(result2.getId());

    Optional<User> dbUser1 = userRepository.findById(result1.getId());
    Optional<User> dbUser2 = userRepository.findById(result2.getId());
    assertThat(dbUser1).isPresent();
    assertThat(dbUser2).isPresent();
    assertThat(dbUser1.get().getName()).isEqualTo(user1.getName());
    assertThat(dbUser2.get().getName()).isEqualTo(user2.getName());
  }

  @Test
  void save_WhenUserWithExistingUuid_ShouldThrowException() {
    User user1 = UserFactory.createUser("User 06", "user06@example.com", "user06");
    User user2 = UserFactory.createUser("User 07", "user07@example.com", "user07");

    User existingUser = userRepository.save(user1);
    user2.setUuid(existingUser.getUuid());

    assertThatThrownBy(() -> saveUser.save(user2))
        .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
  }

  @Test
  void save_WhenUserWithExistingEmail_ShouldThrowException() {
    User user1 = UserFactory.createUser("User 08", "user08@example.com", "user08");
    User user2 = UserFactory.createUser("User 09", "user09@example.com", "user09");

    userRepository.save(user1);
    user2.setEmail(user1.getEmail());

    assertThatThrownBy(() -> saveUser.save(user2))
        .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
  }

  @Test
  void save_WhenUserWithExistingUsername_ShouldThrowException() {
    User user1 = UserFactory.createUser("User 10", "user10@example.com", "user10");
    User user2 = UserFactory.createUser("User 11", "user11@example.com", "user11");

    userRepository.save(user1);
    user2.setUsername(user1.getUsername());

    assertThatThrownBy(() -> saveUser.save(user2))
        .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
  }
}