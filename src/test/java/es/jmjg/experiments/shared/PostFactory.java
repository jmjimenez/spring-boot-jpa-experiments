package es.jmjg.experiments.shared;

import java.util.List;
import java.util.UUID;

import es.jmjg.experiments.application.post.dto.DeletePostDto;
import es.jmjg.experiments.application.post.dto.SavePostDto;
import es.jmjg.experiments.application.post.dto.UpdatePostDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.user.entity.User;

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

  public static SavePostDto createSavePostDto(User user, String title, String body) {
    return new SavePostDto(
        UUID.randomUUID(),
        title,
        body,
        AuthenticatedUserFactory.createAuthenticatedUserDto(user),
        List.of());
  }

  public static SavePostDto createSavePostDto(User user, String title, String body, List<String> tagNames) {
    return new SavePostDto(
        UUID.randomUUID(),
        title,
        body,
        AuthenticatedUserFactory.createAuthenticatedUserDto(user),
        tagNames);
  }

  public static UpdatePostDto createPostUpdateDto(UUID uuid, String title, String body, User authenticatedUser) {
    return new UpdatePostDto(
        uuid,
        title,
        body,
        List.of(),
        AuthenticatedUserFactory.createAuthenticatedUserDto(authenticatedUser));
  }

  public static UpdatePostDto createPostUpdateDto(UUID uuid, String title, String body, List<String> tagNames, User authenticatedUser) {
    return new UpdatePostDto(
        uuid,
        title,
        body,
        tagNames,
        AuthenticatedUserFactory.createAuthenticatedUserDto(authenticatedUser));
  }

  public static DeletePostDto createDeletePostDto(UUID uuid, User authenticatedUser) {
    return new DeletePostDto(
        uuid,
        AuthenticatedUserFactory.createAuthenticatedUserDto(authenticatedUser));
  }
}
