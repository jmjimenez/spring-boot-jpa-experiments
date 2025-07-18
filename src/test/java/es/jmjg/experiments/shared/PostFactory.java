package es.jmjg.experiments.shared;

import java.util.UUID;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;

/**
 * Factory class for creating test Post instances with various configurations. Provides methods to
 * create posts with different content and user associations.
 */
public class PostFactory {

  /**
   * Creates a basic post with default values.
   *
   * @param user the user to associate with the post
   * @return a new Post instance with default values
   */
  public static Post createBasicPost(User user) {
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(user);
    post.setTitle("Test Post");
    post.setBody("Test Body");
    return post;
  }

  /**
   * Creates a post with custom title and body.
   *
   * @param user the user to associate with the post
   * @param title the title for the post
   * @param body the body content for the post
   * @return a new Post instance with the specified content
   */
  public static Post createPost(User user, String title, String body) {
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(user);
    post.setTitle(title);
    post.setBody(body);
    return post;
  }

  /**
   * Creates a post with a specific UUID.
   *
   * @param user the user to associate with the post
   * @param uuid the UUID for the post
   * @param title the title for the post
   * @param body the body content for the post
   * @return a new Post instance with the specified UUID and content
   */
  public static Post createPost(User user, UUID uuid, String title, String body) {
    Post post = new Post();
    post.setUuid(uuid);
    post.setUser(user);
    post.setTitle(title);
    post.setBody(body);
    return post;
  }

  /**
   * Creates a post without a user (for testing scenarios where user is set later).
   *
   * @param title the title for the post
   * @param body the body content for the post
   * @return a new Post instance without a user
   */
  public static Post createPostWithoutUser(String title, String body) {
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setTitle(title);
    post.setBody(body);
    return post;
  }

  /**
   * Creates a Spring Boot related post.
   *
   * @param user the user to associate with the post
   * @return a new Post instance with Spring Boot content
   */
  public static Post createSpringBootPost(User user) {
    return createPost(
        user, "Spring Boot Tutorial", "Learn how to build applications with Spring Boot framework");
  }

  /**
   * Creates a JPA related post.
   *
   * @param user the user to associate with the post
   * @return a new Post instance with JPA content
   */
  public static Post createJpaPost(User user) {
    return createPost(
        user, "JPA Best Practices", "Understanding JPA and database integration with Spring Boot");
  }

  /**
   * Creates a Java programming related post.
   *
   * @param user the user to associate with the post
   * @return a new Post instance with Java programming content
   */
  public static Post createJavaProgrammingPost(User user) {
    return createPost(
        user,
        "Java Programming Guide",
        "Complete guide to Java programming language and best practices");
  }

  /**
   * Creates an advanced Java features post.
   *
   * @param user the user to associate with the post
   * @return a new Post instance with advanced Java content
   */
  public static Post createAdvancedJavaPost(User user) {
    return createPost(
        user,
        "Advanced Java Features",
        "Exploring advanced features in Java 21 and modern development");
  }
}
