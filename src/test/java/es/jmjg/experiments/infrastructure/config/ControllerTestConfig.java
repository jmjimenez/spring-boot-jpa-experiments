package es.jmjg.experiments.infrastructure.config;

import static org.mockito.Mockito.*;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import es.jmjg.experiments.application.post.DeletePostById;
import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.application.post.FindPosts;
import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.UpdatePost;
import es.jmjg.experiments.application.user.DeleteUserByUuid;
import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.FindUserById;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.infrastructure.controller.mapper.PostMapper;
import es.jmjg.experiments.infrastructure.controller.mapper.UserMapper;

@TestConfiguration
public class ControllerTestConfig {

  @Bean
  @Primary
  public DeletePostById deletePostById() {
    return mock(DeletePostById.class);
  }

  @Bean
  @Primary
  public FindPosts findPosts() {
    return mock(FindPosts.class);
  }

  @Bean
  @Primary
  public UpdatePost updatePost() {
    return mock(UpdatePost.class);
  }

  @Bean
  @Primary
  public SavePost savePost() {
    return mock(SavePost.class);
  }

  @Bean
  @Primary
  public FindPostByUuid findPostByUuid() {
    return mock(FindPostByUuid.class);
  }

  @Bean
  @Primary
  public FindAllPosts findAllPosts() {
    return mock(FindAllPosts.class);
  }

  @Bean
  public PostMapper postMapper() {
    return new PostMapper();
  }

  @Bean
  @Primary
  public UserMapper userMapper() {
    return new UserMapper();
  }

  @Bean
  @Primary
  public SaveUser saveUser() {
    return mock(SaveUser.class);
  }

  @Bean
  @Primary
  public UpdateUser updateUser() {
    return mock(UpdateUser.class);
  }

  @Bean
  @Primary
  public FindUserById findUserById() {
    return mock(FindUserById.class);
  }

  @Bean
  @Primary
  public FindUserByUuid findUserByUuid() {
    return mock(FindUserByUuid.class);
  }

  @Bean
  @Primary
  public FindUserByEmail findUserByEmail() {
    return mock(FindUserByEmail.class);
  }

  @Bean
  @Primary
  public FindUserByUsername findUserByUsername() {
    return mock(FindUserByUsername.class);
  }

  @Bean
  @Primary
  public FindAllUsers findAllUsers() {
    return mock(FindAllUsers.class);
  }

  @Bean
  @Primary
  public DeleteUserByUuid deleteUserByUuid() {
    return mock(DeleteUserByUuid.class);
  }
}
