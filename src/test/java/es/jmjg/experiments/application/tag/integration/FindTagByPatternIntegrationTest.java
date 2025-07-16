package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.tag.FindTagByPattern;
import es.jmjg.experiments.domain.Tag;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.TagFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindTagByPatternIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindTagByPattern findTagByPattern;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private Environment environment;

  @BeforeEach
  void setUp() {
    // Clean up before each test
    tagRepository.deleteAll();
  }

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
    // Given
    Tag javaTag = TagFactory.createJavaTag();
    Tag springBootTag = TagFactory.createSpringBootTag();
    Tag jpaTag = TagFactory.createJpaTag();

    tagRepository.save(javaTag);
    tagRepository.save(springBootTag);
    tagRepository.save(jpaTag);

    // When
    List<Tag> result = findTagByPattern.findByPattern("java");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("java");
  }

  @Test
  void findByPattern_WhenPatternMatchesMultipleTags_ShouldReturnAllMatchingTags() {
    // Given
    Tag javaTag = TagFactory.createJavaTag();
    Tag springBootTag = TagFactory.createSpringBootTag();
    Tag jpaTag = TagFactory.createJpaTag();

    tagRepository.save(javaTag);
    tagRepository.save(springBootTag);
    tagRepository.save(jpaTag);

    // When
    List<Tag> result = findTagByPattern.findByPattern("j");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2); // java and jpa
    assertThat(result).extracting("name").containsExactlyInAnyOrder("java", "jpa");
  }

  @Test
  void findByPattern_WhenPatternIsCaseInsensitive_ShouldReturnMatchingTags() {
    // Given
    Tag javaTag = TagFactory.createJavaTag();
    tagRepository.save(javaTag);

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
    // Given
    Tag javaTag = TagFactory.createJavaTag();
    tagRepository.save(javaTag);

    // When
    List<Tag> result = findTagByPattern.findByPattern("nonexistent");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByPattern_WhenPatternIsTrimmed_ShouldWorkCorrectly() {
    // Given
    Tag javaTag = TagFactory.createJavaTag();
    tagRepository.save(javaTag);

    // When
    List<Tag> result = findTagByPattern.findByPattern("  java  ");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("java");
  }

  @Test
  void findByPattern_WhenPatternMatchesPartial_ShouldReturnMatchingTags() {
    // Given
    Tag springBootTag = TagFactory.createSpringBootTag();
    Tag jpaTag = TagFactory.createJpaTag();
    Tag databaseTag = TagFactory.createDatabaseTag();

    tagRepository.save(springBootTag);
    tagRepository.save(jpaTag);
    tagRepository.save(databaseTag);

    // When
    List<Tag> result = findTagByPattern.findByPattern("boot");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("spring-boot");
  }
}