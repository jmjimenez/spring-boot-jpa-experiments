package es.jmjg.experiments.shared;

import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import es.jmjg.experiments.application.post.SavePostDto;
import es.jmjg.experiments.application.post.UpdatePostDto;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.security.JwtUserDetailsService;

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
        null);
  }

  public static UpdatePostDto createPostUpdateDto(UUID uuid, String title, String body, User authenticatedUser) {
    return new UpdatePostDto(
        uuid,
        title,
        body,
        null,
        generateRegularUserJwtUserDetails(authenticatedUser));
  }

  private static JwtUserDetails generateRegularUserJwtUserDetails(User authenticatedUser) {
    return new JwtUserDetails(
        authenticatedUser.getUuid(),
        authenticatedUser.getUsername(),
        authenticatedUser.getPassword(),
        authenticatedUser.getUsername().equals(TestDataSamples.ADMIN_USERNAME)
            ? java.util.List.of(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_ADMIN))
            : java.util.List.of(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_USER)));
  }
}