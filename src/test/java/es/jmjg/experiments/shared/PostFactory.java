package es.jmjg.experiments.shared;

import java.util.List;
import java.util.UUID;

import es.jmjg.experiments.application.post.dto.DeletePostDto;
import es.jmjg.experiments.application.post.dto.SavePostDto;
import es.jmjg.experiments.application.post.dto.UpdatePostDto;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;

public class PostFactory {

  public static Post createBasicPost(User user) {
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(user);
    post.setTitle("Test Post");
    post.setBody("Test Body");
    return post;
  }

  public static Post createPost(User user, String title, String body) {
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(user);
    post.setTitle(title);
    post.setBody(body);
    return post;
  }

  public static Post createPost(User user, UUID uuid, String title, String body) {
    Post post = new Post();
    post.setUuid(uuid);
    post.setUser(user);
    post.setTitle(title);
    post.setBody(body);
    return post;
  }

  public static Post createPostWithoutUser(String title, String body) {
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setTitle(title);
    post.setBody(body);
    return post;
  }

  public static Post createSpringBootPost(User user) {
    return createPost(
        user, UUID.randomUUID(), "Spring Boot Tutorial", "Learn how to build applications with Spring Boot framework");
  }

  public static Post createJpaPost(User user) {
    return createPost(
        user, UUID.randomUUID(), "JPA Best Practices", "Understanding JPA and database integration with Spring Boot");
  }

  public static Post createJavaProgrammingPost(User user) {
    return createPost(
        user,
        UUID.randomUUID(),
        "Java Programming Guide",
        "Complete guide to Java programming language and best practices");
  }

  public static Post createAdvancedJavaPost(User user) {
    return createPost(
        user,
        UUID.randomUUID(),
        "Advanced Java Features",
        "Exploring advanced features in Java 21 and modern development");
  }

  public static SavePostDto createSavePostDto(User user, String title, String body) {
    return new SavePostDto(
        UUID.randomUUID(),
        title,
        body,
        user.getUuid(),
        List.of());
  }

  public static UpdatePostDto createPostUpdateDto(UUID uuid, String title, String body, User authenticatedUser) {
    return new UpdatePostDto(
        uuid,
        title,
        body,
        List.of(),
        UserDetailsFactory.createJwtUserDetails(authenticatedUser));
  }

  public static DeletePostDto createDeletePostDto(UUID uuid, User authenticatedUser) {
    return new DeletePostDto(
        uuid,
        UserDetailsFactory.createAuthenticatedUserDto(authenticatedUser));
  }
}