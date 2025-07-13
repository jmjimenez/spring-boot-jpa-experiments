package es.jmjg.experiments.infrastructure.controller;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.controller.dto.PostRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.PostResponseDto;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PostControllerIntegrationTest extends TestContainersConfig {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  @Test
  void shouldUseTestProfile() {
    // Verify that the test profile is active
    String[] activeProfiles = environment.getActiveProfiles();
    assertThat(activeProfiles).contains("test");
  }

  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

  @Test
  void shouldReturnAllPosts() {
    ResponseEntity<PostResponseDto[]> response = restTemplate.getForEntity("/api/posts", PostResponseDto[].class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PostResponseDto[] posts = response.getBody();
    assertThat(posts).isNotNull().satisfies(p -> assertThat(p.length).isGreaterThan(0));
  }

  @Test
  void shouldReturnPostByUuid() {
    ResponseEntity<PostResponseDto> response = restTemplate.getForEntity(
        "/api/posts/550e8400-e29b-41d4-a716-446655440007", PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PostResponseDto post = response.getBody();
    assertThat(post).isNotNull().satisfies(p -> assertThat(p.getTitle()).isEqualTo("qui est esse"));
  }

  @Test
  void shouldReturnNotFoundForInvalidUuid() {
    String randomUuid = java.util.UUID.randomUUID().toString();
    ResponseEntity<PostResponseDto> response = restTemplate.getForEntity("/api/posts/" + randomUuid,
        PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldSearchPosts() {
    ResponseEntity<PostResponseDto[]> response = restTemplate.getForEntity("/api/posts/search?q=Spring&limit=5",
        PostResponseDto[].class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PostResponseDto[] posts = response.getBody();
    assertThat(posts).isNotNull();
  }

  @Test
  @DirtiesContext
  void shouldCreateNewPostWhenPostIsValid() {
    User user = new User(null, UUID.randomUUID(), "Test User", "test@example.com", "testuser", null);
    user = userRepository.save(user);
    final UUID userUuid = user.getUuid();

    PostRequestDto postDto = new PostRequestDto(null, java.util.UUID.randomUUID(), userUuid, "101 Title", "101 Body");

    ResponseEntity<PostResponseDto> response = restTemplate.exchange(
        "/api/posts", HttpMethod.POST, new HttpEntity<>(postDto), PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    PostResponseDto post = response.getBody();
    assertThat(post).isNotNull();

    assertThat(post)
        .isNotNull()
        .satisfies(
            body -> {
              assertThat(body.getId()).isNotNull();
              assertThat(body.getUserId()).isEqualTo(userUuid);
              assertThat(body.getTitle()).isEqualTo("101 Title");
              assertThat(body.getBody()).isEqualTo("101 Body");
            });

    // Clean up manually - only delete the specific post that was created
    if (post != null) {
      final Integer postId = post.getId();
      postRepository.deleteById(postId);
    }
  }

  @Test
  @Transactional
  @Rollback
  void shouldNotCreateNewPostWhenValidationFails() {
    User user = new User(1, UUID.randomUUID(), "Test User", "test@example.com", "testuser", null);
    user = userRepository.save(user);
    PostRequestDto postDto = new PostRequestDto(101, java.util.UUID.randomUUID(), user.getUuid(), "", "");
    ResponseEntity<PostResponseDto> response = restTemplate.exchange(
        "/api/posts", HttpMethod.POST, new HttpEntity<>(postDto), PostResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @DirtiesContext
  void shouldUpdatePostWhenPostExists() {
    User user = new User(null, UUID.randomUUID(), "Test User", "test@example.com", "testuser", null);
    user = userRepository.save(user);
    final UUID userUuid = user.getUuid();

    PostRequestDto postDto = new PostRequestDto(
        null, java.util.UUID.randomUUID(), userUuid, "Updated Title", "Updated Body");

    ResponseEntity<PostResponseDto> response = restTemplate.exchange(
        "/api/posts/1", HttpMethod.PUT, new HttpEntity<>(postDto), PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PostResponseDto post = response.getBody();
    assertThat(post)
        .isNotNull()
        .satisfies(
            p -> {
              assertThat(p.getTitle()).isEqualTo("Updated Title");
              assertThat(p.getBody()).isEqualTo("Updated Body");
            });
  }
}
