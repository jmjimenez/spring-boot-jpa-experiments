package es.jmjg.experiments.infrastructure.controller;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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

  // TODO: replace with RestClient
  @Autowired
  TestRestTemplate restTemplate;

  @Autowired
  PostRepository postRepository;

  @Autowired
  UserRepository userRepository;

  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

  @Test
  @Transactional
  @Rollback
  void shouldFindAllPosts() {
    PostResponseDto[] posts = restTemplate.getForObject("/api/posts", PostResponseDto[].class);
    // The data loader loads 100 posts at startup, so we should have at least 100
    assertThat(posts.length).isEqualTo(50);
  }

  @Test
  @Transactional
  @Rollback
  void shouldFindPostWhenValidPostUUID() {
    ResponseEntity<PostResponseDto> response =
        restTemplate.exchange("/api/posts/550e8400-e29b-41d4-a716-446655440001", HttpMethod.GET,
            null, PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @Transactional
  @Rollback
  void shouldThrowNotFoundWhenInvalidPostID() {
    ResponseEntity<PostResponseDto> response = restTemplate.exchange(
        "/api/posts/" + java.util.UUID.randomUUID(), HttpMethod.GET, null, PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DirtiesContext
  void shouldCreateNewPostWhenPostIsValid() {
    User user = new User(null, "Test User", "test@example.com", "testuser", null);
    user = userRepository.save(user);
    final Integer userId = user.getId();

    PostRequestDto postDto =
        new PostRequestDto(null, java.util.UUID.randomUUID(), userId, "101 Title", "101 Body");

    ResponseEntity<PostResponseDto> response = restTemplate.exchange("/api/posts", HttpMethod.POST,
        new HttpEntity<>(postDto), PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    PostResponseDto post = response.getBody();
    assertThat(post).isNotNull();

    assertThat(post).isNotNull().satisfies(body -> {
      assertThat(body.getId()).isNotNull();
      assertThat(body.getUserId()).isEqualTo(userId);
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
    User user = new User(1, "Test User", "test@example.com", "testuser", null);
    user = userRepository.save(user);
    PostRequestDto postDto =
        new PostRequestDto(101, java.util.UUID.randomUUID(), user.getId(), "", "");
    ResponseEntity<PostResponseDto> response = restTemplate.exchange("/api/posts", HttpMethod.POST,
        new HttpEntity<>(postDto), PostResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @DirtiesContext
  void shouldUpdatePostWhenPostIsValid() {
    User user = new User(null, "Test User", "test@example.com", "testuser", null);
    user = userRepository.save(user);
    final Integer userId = user.getId();

    ResponseEntity<PostResponseDto> response =
        restTemplate.exchange("/api/posts/550e8400-e29b-41d4-a716-446655440001", HttpMethod.GET,
            null, PostResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    PostResponseDto existing = response.getBody();
    assertThat(existing).isNotNull();
    if (existing != null) {
      PostRequestDto updatedDto = new PostRequestDto(existing.getId(), java.util.UUID.randomUUID(),
          userId, "NEW POST TITLE #1", "NEW POST BODY #1");

      ResponseEntity<PostResponseDto> updateResponse = restTemplate.exchange("/api/posts/1",
          HttpMethod.PUT, new HttpEntity<>(updatedDto), PostResponseDto.class);
      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(updateResponse.getBody()).isNotNull().satisfies(body -> {
        assertThat(body.getId()).isEqualTo(existing.getId());
        assertThat(body.getUserId()).isEqualTo(userId);
        assertThat(body.getTitle()).isEqualTo("NEW POST TITLE #1");
        assertThat(body.getBody()).isEqualTo("NEW POST BODY #1");
      });
    }
  }

  @Test
  @Transactional
  @Rollback
  void shouldDeleteWithValidID() {
    ResponseEntity<Void> response =
        restTemplate.exchange("/api/posts/88", HttpMethod.DELETE, null, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
