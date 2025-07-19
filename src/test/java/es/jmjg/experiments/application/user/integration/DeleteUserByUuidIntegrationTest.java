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

import es.jmjg.experiments.application.user.DeleteUserByUuid;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DeleteUserByUuidIntegrationTest extends TestContainersConfig {

  @Autowired
  private DeleteUserByUuid deleteUserByUuid;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser;
  private UUID testUuid;

  @BeforeEach
  void setUp() {
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
  void deleteByUuid_WhenUserExists_ShouldDeleteUser() {
    // Given
    userRepository.save(testUser);
    assertThat(userRepository.findByUuid(testUuid)).isPresent();

    // When
    deleteUserByUuid.deleteByUuid(testUuid);

    // Then
    Optional<User> deletedUser = userRepository.findByUuid(testUuid);
    assertThat(deletedUser).isEmpty();
  }

  @Test
  void deleteByUuid_WhenUserDoesNotExist_ShouldNotThrowException() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    assertThat(userRepository.findByUuid(nonExistentUuid)).isEmpty();

    // When & Then
    assertThatCode(() -> deleteUserByUuid.deleteByUuid(nonExistentUuid))
        .doesNotThrowAnyException();
  }

  @Test
  void deleteByUuid_WhenUuidIsNull_ShouldNotThrowException() {
    // When & Then
    assertThatCode(() -> deleteUserByUuid.deleteByUuid(null))
        .doesNotThrowAnyException();
  }

  @Test
  void deleteByUuid_WhenMultipleUsersExist_ShouldDeleteOnlyTargetUser() {
    // Given
    UUID secondUuid = UUID.randomUUID();
    User secondUser =
        UserFactory.createUser(secondUuid, "Second User", "second@example.com", "seconduser");

    userRepository.save(testUser);
    userRepository.save(secondUser);

    assertThat(userRepository.findByUuid(testUuid)).isPresent();
    assertThat(userRepository.findByUuid(secondUuid)).isPresent();

    // When
    deleteUserByUuid.deleteByUuid(testUuid);

    // Then
    assertThat(userRepository.findByUuid(testUuid)).isEmpty();
    assertThat(userRepository.findByUuid(secondUuid)).isPresent();
    assertThat(userRepository.findByUuid(secondUuid).get().getName()).isEqualTo("Second User");
  }

  @Test
  void deleteByUuid_WhenUserIsDeleted_ShouldNotAffectOtherOperations() {
    // Given
    userRepository.save(testUser);
    assertThat(userRepository.findByUuid(testUuid)).isPresent();

    // When
    deleteUserByUuid.deleteByUuid(testUuid);

    // Then
    assertThat(userRepository.findByUuid(testUuid)).isEmpty();

    // Verify that other operations still work
    User newUser = UserFactory.createUser("New User", "new@example.com", "newuser");
    User savedNewUser = userRepository.save(newUser);
    assertThat(savedNewUser.getId()).isNotNull();
    assertThat(userRepository.findByUuid(newUser.getUuid())).isPresent();
  }

  @Test
  void deleteByUuid_WhenCalledMultipleTimes_ShouldHandleGracefully() {
    // Given
    userRepository.save(testUser);
    assertThat(userRepository.findByUuid(testUuid)).isPresent();

    // When
    deleteUserByUuid.deleteByUuid(testUuid);
    deleteUserByUuid.deleteByUuid(testUuid); // Second call on already deleted user

    // Then
    assertThat(userRepository.findByUuid(testUuid)).isEmpty();
  }

  @Test
  void deleteByUuid_WhenDatabaseIsEmpty_ShouldNotThrowException() {
    // When & Then
    assertThatCode(() -> deleteUserByUuid.deleteByUuid(UUID.randomUUID()))
        .doesNotThrowAnyException();
  }
}
