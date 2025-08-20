package es.jmjg.experiments.shared;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.controller.dto.AuthenticationRequest;
import es.jmjg.experiments.infrastructure.controller.dto.AuthenticationResponse;

//TODO: add archUnit tests like in https://www.wimdeblauwe.com/blog/2025/07/30/how-i-test-production-ready-spring-boot-applications/

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseControllerIntegration extends TestContainersConfig {

  // Sample tags from Flyway migration data
  protected static final UUID TECHNOLOGY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440056");
  protected static final UUID JAVA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440058");
  protected static final UUID DEVELOPER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440071");
  protected static final UUID NOT_USED_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440072");

  // Sample users from Flyway migration data
  protected static final UUID LEANNE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
  protected static final String LEANNE_NAME = "Leanne Graham";
  protected static final String LEANNE_EMAIL = "leanne.graham@example.com";
  protected static final String LEANNE_USERNAME = "leanne_graham";

  protected static final String ERVIN_NAME = "Ervin Howell";
  protected static final String ERVIN_EMAIL = "ervin.howell@example.com";
  protected static final String ERVIN_USERNAME = "ervin_howell";

  protected static final String CLEMENTINE_NAME = "Clementine Bauch";
  protected static final String CLEMENTINE_EMAIL = "clementine.bauch@example.com";
  protected static final String CLEMENTINE_USERNAME = "clementine_bauch";

  protected static final String ADMIN_USERNAME = "admin";
  protected static final String ADMIN_PASSWORD = "testpass";

  protected static final UUID PATRICIA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");

  protected static final UUID CHELSEY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");

  // Sample posts from Flyway migration data
  protected static final UUID POST_1_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440006");
  protected static final String POST_1_TITLE = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";
  protected static final UUID POST_2_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440007");
  protected static final String POST_2_TITLE = "qui est esse";
  protected static final UUID POST_3_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440008");
  protected static final String POST_3_TITLE = "ea molestias quasi exercitationem repellat qui ipsa sit aut";

  // Sample tag names from Flyway migration data
  protected static final String TECHNOLOGY_TAG_NAME = "technology";
  protected static final String SPRING_BOOT_TAG_NAME = "spring-boot";
  protected static final String JPA_TAG_NAME = "jpa";

  // Sample search terms and expected counts from Flyway migration data
  protected static final String SEARCH_TERM_SUNT = "fugiat";
  protected static final int EXPECTED_SUNT_SEARCH_COUNT = 3;

  @Autowired
  protected Environment environment;

  @Autowired
  protected TestRestTemplate restTemplate;

  @Test
  void shouldUseTestProfile() {
    String[] activeProfiles = environment.getActiveProfiles();
    assertThat(activeProfiles).contains("test");
  }

  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

  @SuppressWarnings("null")
  protected String getAccessToken(final String username, final String password) {
    AuthenticationRequest authRequest = new AuthenticationRequest();
    authRequest.setLogin(username);
    authRequest.setPassword(password);

    ResponseEntity<AuthenticationResponse> authResponse = restTemplate.postForEntity(
        "/authenticate",
        new HttpEntity<>(authRequest),
        AuthenticationResponse.class);

    assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(authResponse.getBody()).isNotNull();
    assertThat(authResponse.getBody().getAccessToken()).isNotEmpty();

    return authResponse.getBody().getAccessToken();
  }

  protected HttpEntity<String> generateRequestWithAccessToken(String username, String password) {
    final String accessToken = getAccessToken(username, password);
    return generateRequestWithAccessToken(accessToken);
  }

  protected HttpEntity<String> generateRequestWithAccessToken(String accessToken) {
    HttpEntity<String> request = new HttpEntity<>(generateHeadersWithAccessToken(accessToken));
    return request;
  }

  protected HttpHeaders generateHeadersWithAccessToken(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    return headers;
  }

  protected <T> HttpEntity<T> createAuthenticatedRequest(String username, String password, T dto) {
    final String accessToken = getAccessToken(username, password);
    return new HttpEntity<>(dto, generateHeadersWithAccessToken(accessToken));
  }
}
