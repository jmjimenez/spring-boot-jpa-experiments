package es.jmjg.experiments.shared;

import es.jmjg.experiments.domain.post.entity.PostUuid;
import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.ArrayList;
import java.util.List;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.user.entity.User;

public class UserFactory {

  public static User generateBasicUserWithPostsAndTags() {
    User testUser = createBasicUser();

    List<Post> testPosts = new ArrayList<>();
    Post post1 = new Post();
    post1.setPostUuid(PostUuid.randomUUID());
    testPosts.add(post1);
    Post post2 = new Post();
    post2.setPostUuid(PostUuid.randomUUID());
    testPosts.add(post2);
    testUser.setPosts(testPosts);

    List<Tag> testTags = new ArrayList<>();
    Tag tag1 = new Tag();
    tag1.setName("technology");
    testTags.add(tag1);
    Tag tag2 = new Tag();
    tag2.setName("java");
    testTags.add(tag2);
    testUser.setTags(testTags);

    return testUser;
  }

  public static User createBasicUser() {
    return createUser(UserUuid.randomUUID(), "Test User", "test@example.com", "testuser");
  }

  public static User createAdminUser() {
    return createUser(UserUuid.randomUUID(), "Admin User", "admin@example.com", "admin");
  }

  public static User createUser(String name, String email, String username) {
    User user = new User();
    user.setUserUuid(UserUuid.randomUUID());
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    user.setPassword("encodedPassword123");
    return user;
  }

  public static User createUser(UserUuid uuid, String name, String email, String username) {
    User user = new User();
    user.setUserUuid(uuid);
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    user.setPassword("encodedPassword123");
    return user;
  }

  public static User createUser(Integer id, String name, String email, String username) {
    User user = new User();
    user.setId(id);
    user.setUserUuid(UserUuid.randomUUID());
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    user.setPassword("encodedPassword123");
    return user;
  }

  public static User createUser(Integer id, UserUuid uuid, String name, String email, String username) {
    User user = new User();
    user.setId(id);
    user.setUserUuid(uuid);
    user.setName(name);
    user.setEmail(email);
    user.setUsername(username);
    user.setPassword("encodedPassword123");
    return user;
  }
}
