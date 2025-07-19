package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.tag.FindTagByPattern;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FindTagByPatternIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindTagByPattern findTagByPattern;

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
  void findByPattern_WhenPatternMatches_ShouldReturnMatchingTags() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("java");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("java");
  }

  @Test
  void findByPattern_WhenPatternMatchesMultipleTags_ShouldReturnAllMatchingTags() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("j%");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2); // java and jpa
    assertThat(result).extracting("name").containsExactlyInAnyOrder("java", "jpa");
  }

  @Test
  void findByPattern_WhenPatternIsCaseInsensitive_ShouldReturnMatchingTags() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("JAVA");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("java");
  }

  @Test
  void findByPattern_WhenPatternIsNull_ShouldReturnEmptyList() {
    // When
    List<Tag> result = findTagByPattern.findByPattern(null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByPattern_WhenPatternIsEmpty_ShouldReturnEmptyList() {
    // When
    List<Tag> result = findTagByPattern.findByPattern("");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByPattern_WhenPatternIsWhitespace_ShouldReturnEmptyList() {
    // When
    List<Tag> result = findTagByPattern.findByPattern("   ");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByPattern_WhenNoMatches_ShouldReturnEmptyList() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("nonexistent");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByPattern_WhenPatternIsTrimmed_ShouldWorkCorrectly() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("  java  ");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("java");
  }

  @Test
  void findByPattern_WhenPatternMatchesPartial_ShouldReturnMatchingTags() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("boot");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("spring-boot");
  }

  @Test
  void findByPattern_WhenPatternMatchesTechnology_ShouldReturnTechnologyTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("technology");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("technology");
  }

  @Test
  void findByPattern_WhenPatternMatchesProgramming_ShouldReturnProgrammingTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("programming");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("programming");
  }

  @Test
  void findByPattern_WhenPatternMatchesDatabase_ShouldReturnDatabaseTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("database");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("database");
  }

  @Test
  void findByPattern_WhenPatternMatchesWebDevelopment_ShouldReturnWebDevelopmentTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("web-development");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("web-development");
  }

  @Test
  void findByPattern_WhenPatternMatchesTutorial_ShouldReturnTutorialTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("tutorial");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("tutorial");
  }

  @Test
  void findByPattern_WhenPatternMatchesBestPractices_ShouldReturnBestPracticesTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("best-practices");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("best-practices");
  }

  @Test
  void findByPattern_WhenPatternMatchesArchitecture_ShouldReturnArchitectureTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("architecture");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("architecture");
  }

  @Test
  void findByPattern_WhenPatternMatchesMicroservices_ShouldReturnMicroservicesTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("microservices");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("microservices");
  }

  @Test
  void findByPattern_WhenPatternMatchesTesting_ShouldReturnTestingTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("testing");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("testing");
  }

  @Test
  void findByPattern_WhenPatternMatchesDevOps_ShouldReturnDevOpsTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("devops");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("devops");
  }

  @Test
  void findByPattern_WhenPatternMatchesApi_ShouldReturnApiTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("api");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("api");
  }

  @Test
  void findByPattern_WhenPatternMatchesSecurity_ShouldReturnSecurityTag() {
    // Given - Using existing Flyway test data
    // When
    List<Tag> result = findTagByPattern.findByPattern("security");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("security");
  }
}