package es.jmjg.experiments.shared;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.infrastructure.config.TestContainersConfig;

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

  protected static final UUID PATRICIA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");

  protected static final UUID CHELSEY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");

  // Sample posts from Flyway migration data
  protected static final UUID POST_2_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440007");
  protected static final String POST_2_TITLE = "qui est esse";

  // Sample tag names from Flyway migration data
  protected static final String TECHNOLOGY_TAG_NAME = "technology";
  protected static final String SPRING_BOOT_TAG_NAME = "spring-boot";
  protected static final String JPA_TAG_NAME = "jpa";

  // Sample search terms and expected counts from Flyway migration data
  protected static final String SEARCH_TERM_SUNT = "fugiat";
  protected static final int EXPECTED_SUNT_SEARCH_COUNT = 3;

  @Autowired
  protected Environment environment;

  // TODO: replace restTemplate with WebTestClient or RestAssured see
  // https://www.wimdeblauwe.com/blog/2025/07/30/how-i-test-production-ready-spring-boot-applications/
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
}
