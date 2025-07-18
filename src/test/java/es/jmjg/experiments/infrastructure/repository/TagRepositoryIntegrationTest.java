package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.shared.TagFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TagRepositoryIntegrationTest extends TestContainersConfig {

  @Autowired
  private TagRepository tagRepository;

  @BeforeEach
  void setUp() {
    // Clean up before each test
    tagRepository.deleteAll();
  }

  @Test
  void shouldSaveAndRetrieveTag() {
    // Given
    Tag tag = TagFactory.createBasicTag();

    // When
    Tag savedTag = tagRepository.save(tag);
    Optional<Tag> foundTag = tagRepository.findById(savedTag.getId());

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo("test-tag");
    assertThat(foundTag.get().getUuid()).isNotNull();
  }

  @Test
  void shouldFindTagByName() {
    // Given
    Tag tag = TagFactory.createTag("java test");
    tagRepository.save(tag);

    // When
    Optional<Tag> foundTag = tagRepository.findByName("java test");

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo("java test");
  }

  @Test
  void shouldFindTagByUuid() {
    // Given
    UUID uuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(uuid, "spring-boot test");
    tagRepository.save(tag);

    // When
    Optional<Tag> foundTag = tagRepository.findByUuid(uuid);

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getUuid()).isEqualTo(uuid);
    assertThat(foundTag.get().getName()).isEqualTo("spring-boot test");
  }

  @Test
  void shouldDeleteTagByUuid() {
    // Given
    UUID uuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(uuid, "jpa test");
    tagRepository.save(tag);  

    // When
    tagRepository.deleteByUuid(uuid);
    Optional<Tag> foundTag = tagRepository.findByUuid(uuid);

    // Then
    assertThat(foundTag).isEmpty();
  }

  @Test
  void shouldNotFindTagByNonExistentName() {
    // When
    Optional<Tag> foundTag = tagRepository.findByName("non-existent");

    // Then
    assertThat(foundTag).isEmpty();
  }

  @Test
  void shouldNotFindTagByNonExistentUuid() {
    // When
    Optional<Tag> foundTag = tagRepository.findByUuid(UUID.randomUUID());

    // Then
    assertThat(foundTag).isEmpty();
  }
}