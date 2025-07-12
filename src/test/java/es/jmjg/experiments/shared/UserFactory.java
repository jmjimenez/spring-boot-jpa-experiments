package es.jmjg.experiments.shared;

import java.util.UUID;

import es.jmjg.experiments.domain.User;

/**
 * Factory class for creating test User instances with various configurations.
 * Provides methods to
 * create users with different attributes and settings.
 */
public class UserFactory {

  /**
   * Creates a basic user with default test values.
   *
   * @return a new User instance with default test values
   */
  public static User createBasicUser() {
    User user = new User();
    user.setUuid(UUID.randomUUID());
    user.setName("Test User");
    user.setEmail("test@example.com");
    user.setUsername("testuser");
    return user;
  }

  /**
   * Creates a user with custom name, email, and username.
   *
   * @param name     the name for the user
   * @param email    the email for the user
   * @param username the username for the user
   * @return a new User instance with the specified attributes
   */
  public static User createUser(String name, String email, String username) {
    User user = new User();
    user.setUuid(UUID.randomUUID());
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    return user;
  }

  /**
   * Creates a user with a specific UUID and custom attributes.
   *
   * @param uuid     the UUID for the user
   * @param name     the name for the user
   * @param email    the email for the user
   * @param username the username for the user
   * @return a new User instance with the specified UUID and attributes
   */
  public static User createUser(UUID uuid, String name, String email, String username) {
    User user = new User();
    user.setUuid(uuid);
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    return user;
  }

  /**
   * Creates a user with a specific ID and custom attributes.
   *
   * @param id       the ID for the user
   * @param name     the name for the user
   * @param email    the email for the user
   * @param username the username for the user
   * @return a new User instance with the specified ID and attributes
   */
  public static User createUser(Integer id, String name, String email, String username) {
    User user = new User();
    user.setId(id);
    user.setUuid(UUID.randomUUID());
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    return user;
  }

  /**
   * Creates a user with both specific ID and UUID.
   *
   * @param id       the ID for the user
   * @param uuid     the UUID for the user
   * @param name     the name for the user
   * @param email    the email for the user
   * @param username the username for the user
   * @return a new User instance with the specified ID, UUID, and attributes
   */
  public static User createUser(Integer id, UUID uuid, String name, String email, String username) {
    User user = new User();
    user.setId(id);
    user.setUuid(uuid);
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    return user;
  }

  /**
   * Creates a John Doe user (commonly used in controller tests).
   *
   * @return a new User instance representing John Doe
   */
  public static User createJohnDoeUser() {
    return createUser("John Doe", "john@example.com", "johndoe");
  }

  /**
   * Creates a John Doe user with a specific ID.
   *
   * @param id the ID for the user
   * @return a new User instance representing John Doe with the specified ID
   */
  public static User createJohnDoeUser(Integer id) {
    return createUser(id, "John Doe", "john@example.com", "johndoe");
  }

  /**
   * Creates a test user with ID 1 (commonly used in controller tests).
   *
   * @return a new User instance with ID 1 and default test values
   */
  public static User createTestUserWithId1() {
    return createUser(1, "Test User", "test@example.com", "testuser");
  }

  /**
   * Creates a user without setting an ID (for integration tests where ID is
   * generated).
   *
   * @param name     the name for the user
   * @param email    the email for the user
   * @param username the username for the user
   * @return a new User instance without an ID
   */
  public static User createUserWithoutId(String name, String email, String username) {
    User user = new User();
    user.setUuid(UUID.randomUUID());
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    return user;
  }
}