package es.jmjg.experiments.infrastructure.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainersConfig {

  @SuppressWarnings("resource")
  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0")
      .withDatabaseName("blog")
      .withUsername("blog")
      .withPassword("secret_password")
      .withReuse(true)
      .withExposedPorts(5432);

  static {
    // Ensure container is started before any tests run
    postgres.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
  }

  public static PostgreSQLContainer<?> getPostgresContainer() {
    return postgres;
  }

  public static int getMappedPort() {
    return postgres.getMappedPort(5432);
  }
}
