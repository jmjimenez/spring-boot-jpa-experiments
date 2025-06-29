package es.jmjg.experiments.infrastructure.controller;

import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    //TODO: replace with RestClient
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    PostRepository postRepository;

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindAllPosts() {
        Post[] posts = restTemplate.getForObject("/api/posts", Post[].class);
        // The data loader loads 100 posts at startup, so we should have at least 100
        assertThat(posts.length).isEqualTo(100);
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindPostWhenValidPostID() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/1", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundWhenInvalidPostID() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/999", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldCreateNewPostWhenPostIsValid() {
        Post post = new Post(null, 1, "101 Title", "101 Body");

        ResponseEntity<Post> response = restTemplate.exchange("/api/posts", HttpMethod.POST, new HttpEntity<>(post), Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(1);
        assertThat(response.getBody().getTitle()).isEqualTo("101 Title");
        assertThat(response.getBody().getBody()).isEqualTo("101 Body");
        
        // Clean up manually - only delete the specific post that was created
        postRepository.deleteById(response.getBody().getId());
    }

    @Test
    @Transactional
    @Rollback
    void shouldNotCreateNewPostWhenValidationFails() {
        Post post = new Post(101,1,"","");
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts", HttpMethod.POST, new HttpEntity<>(post), Post.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdatePostWhenPostIsValid() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/99", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Post existing = response.getBody();
        assertThat(existing).isNotNull();
        Post updated = new Post(existing.getId(),existing.getUserId(),"NEW POST TITLE #1", "NEW POST BODY #1");

        ResponseEntity<Post> updateResponse = restTemplate.exchange("/api/posts/99", HttpMethod.PUT, new HttpEntity<>(updated), Post.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().getId()).isEqualTo(99);
        assertThat(updateResponse.getBody().getUserId()).isEqualTo(10);
        assertThat(updateResponse.getBody().getTitle()).isEqualTo("NEW POST TITLE #1");
        assertThat(updateResponse.getBody().getBody()).isEqualTo("NEW POST BODY #1");
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteWithValidID() {
        ResponseEntity<Void> response = restTemplate.exchange("/api/posts/88", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}