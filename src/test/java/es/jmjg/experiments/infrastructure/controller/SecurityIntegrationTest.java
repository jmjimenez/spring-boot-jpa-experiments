package es.jmjg.experiments.infrastructure.controller;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.jmjg.experiments.infrastructure.controller.dto.AuthenticationRequest;
import es.jmjg.experiments.shared.BaseControllerIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

class SecurityIntegrationTest extends BaseControllerIntegration {

  private static final String AUTHENTICATE_ENDPOINT = "/authenticate";
  private static final String PROTECTED_ENDPOINT = "/api/posts/" + TestDataSamples.POST_3_UUID;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void accessProtectedEndpoint_WithoutToken_ShouldReturnUnauthorized() throws Exception {
    // When & Then
    ResponseEntity<String> response = restTemplate.exchange(PROTECTED_ENDPOINT, HttpMethod.DELETE, null, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void accessProtectedEndpoint_WithValidToken_ShouldReturnOk() throws Exception {
    // Given - Get a valid token
    AuthenticationRequest authRequest = createAuthenticationRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

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
    ResponseEntity<String> protectedResponse = restTemplate.exchange(PROTECTED_ENDPOINT, HttpMethod.DELETE,
        protectedRequest, String.class);
    assertThat(protectedResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void accessProtectedEndpoint_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
    // When & Then
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("invalid-token");
    HttpEntity<String> request = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(PROTECTED_ENDPOINT, HttpMethod.DELETE, request,
        String.class);
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
    AuthenticationRequest request = createAuthenticationRequest(TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD);

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
    AuthenticationRequest request = createAuthenticationRequest(TestDataSamples.ADMIN_USERNAME, "wrongpassword");

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
    AuthenticationRequest request = createAuthenticationRequest("nonexistentuser", TestDataSamples.ADMIN_PASSWORD);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AuthenticationRequest> httpRequest = new HttpEntity<>(request, headers);

    // When & Then
    ResponseEntity<String> response = restTemplate.postForEntity(AUTHENTICATE_ENDPOINT, httpRequest, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private AuthenticationRequest createAuthenticationRequest(String username, String password) {
    AuthenticationRequest request = new AuthenticationRequest();
    request.setLogin(username);
    request.setPassword(password);
    return request;
  }
}
