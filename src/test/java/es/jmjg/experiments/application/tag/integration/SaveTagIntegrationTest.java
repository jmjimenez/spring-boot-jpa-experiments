package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.tag.SaveTag;
import es.jmjg.experiments.domain.Tag;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.TagFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SaveTagIntegrationTest extends TestContainersConfig {

  @Autowired
  private SaveTag saveTag;

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
  void save_ShouldSaveAndReturnTag() {
    // Given
    Tag tag = TagFactory.createBasicTag();

    // When
    Tag savedTag = saveTag.save(tag);

    // Then
    assertThat(savedTag).isNotNull();
    assertThat(savedTag.getId()).isNotNull();
    assertThat(savedTag.getName()).isEqualTo("test-tag");
    assertThat(savedTag.getUuid()).isNotNull();

    // Verify we can retrieve it from the database
    var foundTag = tagRepository.findById(savedTag.getId());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo("test-tag");
    assertThat(foundTag.get().getUuid()).isEqualTo(savedTag.getUuid());
  }

  @Test
  void save_WithCustomTag_ShouldSaveAndReturnTag() {
    // Given
    Tag tag = TagFactory.createTag("custom-tag");

    // When
    Tag savedTag = saveTag.save(tag);

    // Then
    assertThat(savedTag).isNotNull();
    assertThat(savedTag.getId()).isNotNull();
    assertThat(savedTag.getName()).isEqualTo("custom-tag");
    assertThat(savedTag.getUuid()).isNotNull();

    // Verify we can retrieve it from the database
    var foundTag = tagRepository.findById(savedTag.getId());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo("custom-tag");
  }

  @Test
  void save_WithTechnologyTag_ShouldSaveAndReturnTag() {
    // Given
    Tag tag = TagFactory.createTechnologyTag();

    // When
    Tag savedTag = saveTag.save(tag);

    // Then
    assertThat(savedTag).isNotNull();
    assertThat(savedTag.getId()).isNotNull();
    assertThat(savedTag.getName()).isEqualTo("technology");
    assertThat(savedTag.getUuid()).isNotNull();

    // Verify we can retrieve it from the database
    var foundTag = tagRepository.findById(savedTag.getId());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo("technology");
  }
}