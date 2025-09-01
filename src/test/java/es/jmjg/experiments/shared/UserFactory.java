package es.jmjg.experiments.shared;

import java.util.UUID;

import es.jmjg.experiments.domain.entity.User;

public class UserFactory {

  public static User createBasicUser() {
    return createUser(UUID.randomUUID(), "Test User", "test@example.com", "testuser");
  }

  public static User createAdminUser() {
    return createUser(UUID.randomUUID(), "Admin User", "admin@example.com", "admin");
  }

  public static User createJohnDoeUser(Integer id) {
    return createUser(id, "John Doe", "john@example.com", "johndoe");
  }

  public static User createUser(String name, String email, String username) {
    User user = new User();
    user.setUuid(UUID.randomUUID());
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    user.setPassword("encodedPassword123");
    return user;
  }

  public static User createUser(UUID uuid, String name, String email, String username) {
    User user = new User();
    user.setUuid(uuid);
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    user.setPassword("encodedPassword123");
    return user;
  }

  public static User createUser(Integer id, String name, String email, String username) {
    User user = new User();
    user.setId(id);
    user.setUuid(UUID.randomUUID());
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    user.setPassword("encodedPassword123");
    return user;
  }

  public static User createUser(Integer id, UUID uuid, String name, String email, String username) {
    User user = new User();
    user.setId(id);
    user.setUuid(uuid);
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    user.setPassword("encodedPassword123");
    return user;
  }
}