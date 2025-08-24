package es.jmjg.experiments.shared;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationRequestDto;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationResponseDto;

//TODO: add archUnit tests like in https://www.wimdeblauwe.com/blog/2025/07/30/how-i-test-production-ready-spring-boot-applications/

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseControllerIntegration extends TestContainersConfig {

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

  protected HttpEntity<String> createAuthenticatedRequest(final String username, final String password) {
    final String accessToken = createAccessToken(username, password);
    return createAuthenticatedRequestWithAccessToken(accessToken);
  }

  protected <T> HttpEntity<T> createAuthenticatedRequest(final String username, final String password, final T dto) {
    final String accessToken = createAccessToken(username, password);
    return createAuthenticatedRequestWithAccessToken(accessToken, dto);
  }

  protected HttpEntity<String> createAuthenticatedRequestWithAccessToken(final String accessToken) {
    HttpEntity<String> request = new HttpEntity<>(generateHeadersWithAccessToken(accessToken));
    return request;
  }

  protected <T> HttpEntity<T> createAuthenticatedRequestWithAccessToken(final String accessToken, final T dto) {
    return new HttpEntity<>(dto, generateHeadersWithAccessToken(accessToken));
  }

  @SuppressWarnings("null")
  protected String createAccessToken(final String username, final String password) {
    AuthenticationRequestDto authRequest = new AuthenticationRequestDto();
    authRequest.setLogin(username);
    authRequest.setPassword(password);

    ResponseEntity<AuthenticationResponseDto> authResponse = restTemplate.postForEntity(
        "/authenticate",
        new HttpEntity<>(authRequest),
        AuthenticationResponseDto.class);

    assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(authResponse.getBody()).isNotNull();
    assertThat(authResponse.getBody().getAccessToken()).isNotEmpty();

    return authResponse.getBody().getAccessToken();
  }

  private HttpHeaders generateHeadersWithAccessToken(final String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    return headers;
  }
}
