package es.jmjg.experiments.infrastructure.controller;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.infrastructure.controller.dto.AuthenticationRequest;
import es.jmjg.experiments.infrastructure.controller.post.mapper.PostMapper;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class SecurityIntegrationTest extends BaseControllerIntegration {

  private static final String AUTHENTICATE_ENDPOINT = "/authenticate";
  private static final String PROTECTED_ENDPOINT = "/api/posts/550e8400-e29b-41d4-a716-446655440006";
  private static final String TEST_USERNAME = TestDataSamples.ADMIN_USERNAME;
  private static final String TEST_PASSWORD = TestDataSamples.ADMIN_PASSWORD;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private FindPostByUuid findPostByUuid;

  @Autowired
  private PostMapper postMapper;

  @Test
  void accessProtectedEndpoint_WithoutToken_ShouldReturnUnauthorized() throws Exception {
    // When & Then
    ResponseEntity<String> response = restTemplate.getForEntity(PROTECTED_ENDPOINT, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void accessProtectedEndpoint_WithValidToken_ShouldReturnOk() throws Exception {
    // Given - Get a valid token
    AuthenticationRequest authRequest = new AuthenticationRequest();
    authRequest.setLogin(TEST_USERNAME);
    authRequest.setPassword(TEST_PASSWORD);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AuthenticationRequest> request = new HttpEntity<>(authRequest, headers);

    ResponseEntity<String> authResponse = restTemplate.postForEntity(AUTHENTICATE_ENDPOINT, request, String.class);
    assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    JsonNode responseJson = objectMapper.readTree(authResponse.getBody());
    String token = responseJson.get("accessToken").asText();

    // When & Then - Use the token to access protected endpoint
    headers.setBearerAuth(token);
    HttpEntity<String> protectedRequest = new HttpEntity<>(headers);
    ResponseEntity<String> protectedResponse = restTemplate.exchange(PROTECTED_ENDPOINT, HttpMethod.GET,
        protectedRequest, String.class);
    assertThat(protectedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void accessProtectedEndpoint_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
    // When & Then
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("invalid-token");
    HttpEntity<String> request = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(PROTECTED_ENDPOINT, HttpMethod.GET, request, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void accessPublicEndpoint_WithoutToken_ShouldReturnOk() throws Exception {
    // When & Then
    ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void authenticate_WithValidCredentials_ShouldReturnJwtToken() throws Exception {
    // Given
    AuthenticationRequest request = new AuthenticationRequest();
    request.setLogin(TEST_USERNAME);
    request.setPassword(TEST_PASSWORD);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AuthenticationRequest> httpRequest = new HttpEntity<>(request, headers);

    // When & Then
    ResponseEntity<String> response = restTemplate.postForEntity(AUTHENTICATE_ENDPOINT, httpRequest, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    JsonNode responseJson = objectMapper.readTree(response.getBody());
    assertThat(responseJson.has("accessToken")).isTrue();
    assertThat(responseJson.get("accessToken").asText()).isNotEmpty();
  }

  @Test
  void authenticate_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
    // Given
    AuthenticationRequest request = new AuthenticationRequest();
    request.setLogin(TEST_USERNAME);
    request.setPassword("wrongpassword");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AuthenticationRequest> httpRequest = new HttpEntity<>(request, headers);

    // When & Then
    ResponseEntity<String> response = restTemplate.postForEntity(AUTHENTICATE_ENDPOINT, httpRequest, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void authenticate_WithNonExistentUser_ShouldReturnUnauthorized() throws Exception {
    // Given
    AuthenticationRequest request = new AuthenticationRequest();
    request.setLogin("nonexistentuser");
    request.setPassword(TEST_PASSWORD);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AuthenticationRequest> httpRequest = new HttpEntity<>(request, headers);

    // When & Then
    ResponseEntity<String> response = restTemplate.postForEntity(AUTHENTICATE_ENDPOINT, httpRequest, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testLazyInitializationException_Reproduction() throws Exception {
    // This test reproduces the LazyInitializationException that occurs in the
    // controller
    System.out.println("=== Testing LazyInitializationException reproduction ===");

    UUID postUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440006");
    System.out.println("Testing with post UUID: " + postUuid);

    try {
      // Step 1: Find the post using the service (this works within transaction)
      System.out.println("Step 1: Finding post using FindPostByUuid service...");
      var postOptional = findPostByUuid.findByUuid(postUuid);

      if (postOptional.isPresent()) {
        Post post = postOptional.get();
        System.out.println("Post found successfully!");
        System.out.println("Post ID: " + post.getId());
        System.out.println("Post Title: " + post.getTitle());

        // Step 2: Try to map the post to DTO (this fails outside transaction)
        System.out.println("Step 2: Attempting to map post to DTO (outside transaction)...");
        try {
          var responseDto = postMapper.toFindByUuidResponseDto(post);
          System.out.println("Mapping successful! Response DTO created.");
          System.out.println("Response DTO User ID: " + responseDto.getUserId());
          System.out.println("Response DTO Tags count: " + responseDto.getTags().size());
        } catch (Exception mappingException) {
          System.out.println("Mapping failed with exception: " + mappingException.getClass().getSimpleName());
          System.out.println("Mapping exception message: " + mappingException.getMessage());

          // This is the expected behavior - LazyInitializationException should occur
          assertThat(mappingException).isInstanceOf(org.hibernate.LazyInitializationException.class);
          System.out.println("✓ LazyInitializationException successfully reproduced!");
          return; // Test passed - we reproduced the issue
        }

        // If we get here, the mapping succeeded, which means the issue might be fixed
        System.out.println("⚠ Mapping succeeded - LazyInitializationException was not reproduced");
      } else {
        System.out.println("Post not found with UUID: " + postUuid);
      }
    } catch (Exception e) {
      System.out.println("Test exception: " + e.getClass().getSimpleName());
      System.out.println("Test exception message: " + e.getMessage());
      throw e;
    }
  }
}
