package es.jmjg.experiments.infrastructure.config;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.auth0.jwt.interfaces.DecodedJWT;

import es.jmjg.experiments.application.post.DeletePost;
import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.application.post.FindPosts;
import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.UpdatePost;
import es.jmjg.experiments.application.tag.DeleteTagByUuid;
import es.jmjg.experiments.application.tag.FindPostsByTag;
import es.jmjg.experiments.application.tag.FindTagByPattern;
import es.jmjg.experiments.application.tag.FindTagByUuid;
import es.jmjg.experiments.application.tag.FindUsersByTag;
import es.jmjg.experiments.application.tag.SaveTag;
import es.jmjg.experiments.application.tag.UpdateTagName;
import es.jmjg.experiments.application.user.DeleteUser;
import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtTokenService;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetailsService;
import es.jmjg.experiments.infrastructure.controller.post.mapper.PostMapper;
import es.jmjg.experiments.infrastructure.controller.tag.mapper.TagMapper;
import es.jmjg.experiments.infrastructure.controller.user.mapper.UserMapper;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;

@TestConfiguration
public class ControllerTestConfig {

  @Bean
  @Primary
  public DeletePost deletePostById() {
    return mock(DeletePost.class);
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
  public UserMapper userMapper(PasswordEncoder passwordEncoder) {
    return new UserMapper(passwordEncoder);
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
  public DeleteUser deleteUser() {
    return mock(DeleteUser.class);
  }

  @Bean
  @Primary
  public SaveTag saveTag() {
    return mock(SaveTag.class);
  }

  @Bean
  @Primary
  public UpdateTagName updateTagName() {
    return mock(UpdateTagName.class);
  }

  @Bean
  @Primary
  public DeleteTagByUuid deleteTagByUuid() {
    return mock(DeleteTagByUuid.class);
  }

  @Bean
  @Primary
  public FindTagByPattern findTagByPattern() {
    return mock(FindTagByPattern.class);
  }

  @Bean
  @Primary
  public FindUsersByTag findUsersByTag() {
    return mock(FindUsersByTag.class);
  }

  @Bean
  @Primary
  public FindPostsByTag findPostsByTag() {
    return mock(FindPostsByTag.class);
  }

  @Bean
  @Primary
  public FindTagByUuid findTagByUuid() {
    return mock(FindTagByUuid.class);
  }

  @Bean
  public TagMapper tagMapper() {
    return new TagMapper();
  }

  @Bean
  @Primary
  public JwtUserDetailsService jwtUserDetailsService() {
    JwtUserDetailsService mockService = mock(JwtUserDetailsService.class);

    // Configure the mock to return user details with ROLE_USER authority for any
    // username
    when(mockService.loadUserByUsername(anyString()))
        .thenAnswer(invocation -> {
          String username = invocation.getArgument(0);

          // Special case for admin user
          if (TestDataSamples.ADMIN_USERNAME.equals(username)) {
            User user = UserFactory.createUser(TestDataSamples.ADMIN_UUID, TestDataSamples.ADMIN_NAME,
                TestDataSamples.ADMIN_EMAIL, TestDataSamples.ADMIN_USERNAME);
            return UserFactory.createUserUserDetails(user);
          }

          // For Leanne user
          if (TestDataSamples.LEANNE_USERNAME.equals(username)) {
            User user = UserFactory.createUser(TestDataSamples.LEANNE_UUID, TestDataSamples.LEANNE_NAME,
                TestDataSamples.LEANNE_EMAIL, TestDataSamples.LEANNE_USERNAME);
            return UserFactory.createUserUserDetails(user);
          }

          // For all other users, return with ROLE_USER authority
          User user = UserFactory.createUser(UUID.randomUUID(), "Test User", "test@example.com", username);
          return UserFactory.createUserUserDetails(user);
        });

    return mockService;
  }

  @Bean
  @Primary
  public JwtTokenService jwtTokenService() {
    JwtTokenService mockService = mock(JwtTokenService.class);

    // Configure the mock to return a valid JWT for any token
    when(mockService.validateToken(anyString()))
        .thenAnswer(invocation -> {
          String token = invocation.getArgument(0);

          // Create a mock DecodedJWT that returns the token as the username
          DecodedJWT mockJwt = mock(DecodedJWT.class);
          when(mockJwt.getSubject()).thenReturn(token); // Use token as username for simplicity

          return mockJwt;
        });

    return mockService;
  }
}
