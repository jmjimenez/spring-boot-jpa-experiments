package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.shared.UserFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest extends TestContainersConfig {

  @Autowired
  UserRepository userRepository;

  private User testUser;
  private User johnDoeUser;
  private UUID testUserUuid;
  private UUID johnDoeUuid;

  @BeforeEach
  void setUp() {
    // Create test users using UserFactory
    testUserUuid = UUID.randomUUID();
    johnDoeUuid = UUID.randomUUID();

    testUser = UserFactory.createUser(testUserUuid, "Test User", "test@example.com", "testuser");
    johnDoeUser = UserFactory.createUser(johnDoeUuid, "John Doe", "john@example.com", "johndoe");

    // Save users to database
    testUser = userRepository.save(testUser);
    johnDoeUser = userRepository.save(johnDoeUser);
  }

  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

  @Test
  void shouldFindUserByEmail() {
    Optional<User> foundUser = userRepository.findByEmail("test@example.com");

    assertTrue(foundUser.isPresent(), "User should be found by email");
    assertEquals("test@example.com", foundUser.get().getEmail(), "Email should match");
    assertEquals("Test User", foundUser.get().getName(), "Name should match");
    assertEquals("testuser", foundUser.get().getUsername(), "Username should match");
  }

  @Test
  void shouldNotFindUserByNonExistentEmail() {
    Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

    assertFalse(foundUser.isPresent(), "User should not be found with non-existent email");
  }

  @Test
  void shouldFindUserByUsername() {
    Optional<User> foundUser = userRepository.findByUsername("johndoe");

    assertTrue(foundUser.isPresent(), "User should be found by username");
    assertEquals("johndoe", foundUser.get().getUsername(), "Username should match");
    assertEquals("John Doe", foundUser.get().getName(), "Name should match");
    assertEquals("john@example.com", foundUser.get().getEmail(), "Email should match");
  }

  @Test
  void shouldNotFindUserByNonExistentUsername() {
    Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");

    assertFalse(foundUser.isPresent(), "User should not be found with non-existent username");
  }

  @Test
  void shouldFindUserByUuid() {
    Optional<User> foundUser = userRepository.findByUuid(testUserUuid);

    assertTrue(foundUser.isPresent(), "User should be found by UUID");
    assertEquals(testUserUuid, foundUser.get().getUuid(), "UUID should match");
    assertEquals("Test User", foundUser.get().getName(), "Name should match");
    assertEquals("test@example.com", foundUser.get().getEmail(), "Email should match");
  }

  @Test
  void shouldNotFindUserByNonExistentUuid() {
    UUID nonExistentUuid = UUID.randomUUID();
    Optional<User> foundUser = userRepository.findByUuid(nonExistentUuid);

    assertFalse(foundUser.isPresent(), "User should not be found with non-existent UUID");
  }

  @Test
  void shouldFindAllUsers() {
    List<User> allUsers = userRepository.findAll();

    // There are 5 users from test data migration + 2 users from setUp = 7 total
    assertThat(allUsers).hasSize(7);
    // Check that our test users are included
    assertThat(allUsers).extracting("email").contains("test@example.com", "john@example.com");
    assertThat(allUsers).extracting("username").contains("testuser", "johndoe");
  }

  @Test
  void shouldSaveNewUser() {
    User newUser = UserFactory.createUser("Jane Smith", "jane@example.com", "janesmith");

    User savedUser = userRepository.save(newUser);

    assertNotNull(savedUser.getId(), "Saved user should have an ID");
    assertNotNull(savedUser.getUuid(), "Saved user should have a UUID");
    assertEquals("Jane Smith", savedUser.getName(), "Name should be preserved");
    assertEquals("jane@example.com", savedUser.getEmail(), "Email should be preserved");
    assertEquals("janesmith", savedUser.getUsername(), "Username should be preserved");
  }

  @Test
  void shouldUpdateExistingUser() {
    User userToUpdate = userRepository.findById(testUser.getId()).orElseThrow();
    userToUpdate.setName("Updated Test User");
    userToUpdate.setEmail("updated@example.com");

    User updatedUser = userRepository.save(userToUpdate);

    assertEquals(testUser.getId(), updatedUser.getId(), "ID should remain the same");
    assertEquals("Updated Test User", updatedUser.getName(), "Name should be updated");
    assertEquals("updated@example.com", updatedUser.getEmail(), "Email should be updated");
  }

  @Test
  void shouldDeleteUser() {
    Integer userIdToDelete = testUser.getId();

    userRepository.deleteById(userIdToDelete);

    Optional<User> deletedUser = userRepository.findById(userIdToDelete);
    assertFalse(deletedUser.isPresent(), "User should be deleted");

    List<User> remainingUsers = userRepository.findAll();
    // There are 5 users from test data migration + 1 user from setUp (after
    // deletion) = 6 total
    assertThat(remainingUsers).hasSize(6);
    // Check that our remaining test user is still there
    assertThat(remainingUsers).extracting("email").contains("john@example.com");
  }

  @Test
  void shouldFindUserById() {
    Optional<User> foundUser = userRepository.findById(testUser.getId());

    assertTrue(foundUser.isPresent(), "User should be found by ID");
    assertEquals(testUser.getId(), foundUser.get().getId(), "ID should match");
    assertEquals("Test User", foundUser.get().getName(), "Name should match");
  }

  @Test
  void shouldNotFindUserByNonExistentId() {
    Optional<User> foundUser = userRepository.findById(999);

    assertFalse(foundUser.isPresent(), "User should not be found with non-existent ID");
  }

  @Test
  void shouldDeleteUserByUuid() {
    UUID uuidToDelete = testUser.getUuid();

    userRepository.deleteByUuid(uuidToDelete);

    Optional<User> deletedUser = userRepository.findByUuid(uuidToDelete);
    assertFalse(deletedUser.isPresent(), "User should be deleted by UUID");

    List<User> remainingUsers = userRepository.findAll();
    // There are 5 users from test data migration + 1 user from setUp (after
    // deletion) = 6 total
    assertThat(remainingUsers).hasSize(6);
    // Check that our remaining test user is still there
    assertThat(remainingUsers).extracting("email").contains("john@example.com");
  }

  @Test
  void shouldDeleteUserByUuidAndNotAffectOtherUsers() {
    UUID uuidToDelete = testUser.getUuid();
    UUID remainingUserUuid = johnDoeUser.getUuid();

    userRepository.deleteByUuid(uuidToDelete);

    // Verify the target user is deleted
    Optional<User> deletedUser = userRepository.findByUuid(uuidToDelete);
    assertFalse(deletedUser.isPresent(), "Target user should be deleted by UUID");

    // Verify the other user is still present
    Optional<User> remainingUser = userRepository.findByUuid(remainingUserUuid);
    assertTrue(remainingUser.isPresent(), "Other user should still be present");
    assertEquals("john@example.com", remainingUser.get().getEmail(),
        "Remaining user email should match");
  }

  @Test
  void shouldHandleDeleteByNonExistentUuid() {
    UUID nonExistentUuid = UUID.randomUUID();
    int initialUserCount = userRepository.findAll().size();

    // This should not throw an exception
    userRepository.deleteByUuid(nonExistentUuid);

    int finalUserCount = userRepository.findAll().size();
    assertEquals(initialUserCount, finalUserCount, "User count should remain the same");
  }

  @Test
  void shouldHandleMultipleUsersWithDifferentEmails() {
    User user1 = UserFactory.createUser("User One", "user1@example.com", "user1");
    User user2 = UserFactory.createUser("User Two", "user2@example.com", "user2");

    userRepository.save(user1);
    userRepository.save(user2);

    Optional<User> foundUser1 = userRepository.findByEmail("user1@example.com");
    Optional<User> foundUser2 = userRepository.findByEmail("user2@example.com");

    assertTrue(foundUser1.isPresent(), "First user should be found");
    assertTrue(foundUser2.isPresent(), "Second user should be found");
    assertEquals("User One", foundUser1.get().getName(), "First user name should match");
    assertEquals("User Two", foundUser2.get().getName(), "Second user name should match");
  }

  @Test
  void shouldHandleMultipleUsersWithDifferentUsernames() {
    User user1 = UserFactory.createUser("User One", "user1@example.com", "user1");
    User user2 = UserFactory.createUser("User Two", "user2@example.com", "user2");

    userRepository.save(user1);
    userRepository.save(user2);

    Optional<User> foundUser1 = userRepository.findByUsername("user1");
    Optional<User> foundUser2 = userRepository.findByUsername("user2");

    assertTrue(foundUser1.isPresent(), "First user should be found");
    assertTrue(foundUser2.isPresent(), "Second user should be found");
    assertEquals("user1", foundUser1.get().getUsername(), "First username should match");
    assertEquals("user2", foundUser2.get().getUsername(), "Second username should match");
  }
}
