package es.jmjg.experiments.infrastructure.config;

import static org.mockito.Mockito.*;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
import es.jmjg.experiments.application.user.DeleteUserByUuid;
import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.FindUserById;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.infrastructure.controller.post.mapper.PostMapper;
import es.jmjg.experiments.infrastructure.controller.tag.mapper.TagMapper;
import es.jmjg.experiments.infrastructure.controller.user.mapper.UserMapper;
import es.jmjg.experiments.infrastructure.security.JwtTokenService;
import es.jmjg.experiments.infrastructure.security.JwtUserDetailsService;

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
  @Primary
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Primary
  public UserMapper userMapper() {
    return new UserMapper(passwordEncoder());
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
  public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authz -> authz
            .anyRequest().permitAll());

    return http.build();
  }

  @Bean
  @Primary
  public AuthenticationManager authenticationManager() {
    return mock(AuthenticationManager.class);
  }

  @Bean
  @Primary
  public JwtUserDetailsService jwtUserDetailsService() {
    return mock(JwtUserDetailsService.class);
  }

  @Bean
  @Primary
  public JwtTokenService jwtTokenService() {
    return mock(JwtTokenService.class);
  }

}
