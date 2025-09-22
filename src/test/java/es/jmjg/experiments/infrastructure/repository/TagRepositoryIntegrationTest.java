package es.jmjg.experiments.infrastructure.repository;

import static es.jmjg.experiments.shared.TestDataSamples.*;
import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.domain.tag.exception.TagInUseException;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.shared.BaseJpaIntegration;

@Import(TagRepositoryImpl.class)
public class TagRepositoryIntegrationTest extends BaseJpaIntegration {

  @Autowired
  private TagRepositoryImpl tagRepository;

  @Test
  void shouldFindTagByName() {
    // When
    Optional<Tag> foundTag = tagRepository.findByName(TAG_TECHNOLOGY);

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(TAG_TECHNOLOGY);
    assertThat(foundTag.get().getUuid()).isEqualTo(TAG_TECHNOLOGY_UUID);
  }

  @Test
  void shouldFindTagByUuid() {
    // When
    Optional<Tag> foundTag = tagRepository.findByUuid(TAG_JAVA_UUID);

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getUuid()).isEqualTo(TAG_JAVA_UUID);
    assertThat(foundTag.get().getName()).isEqualTo(TestDataSamples.TAG_JAVA);
  }

  @Test
  void shouldFindMultipleTagsByUuid() {
    // When
    Optional<Tag> springBootTag = tagRepository.findByUuid(TAG_SPRING_BOOT_UUID);
    Optional<Tag> jpaTag = tagRepository.findByUuid(TAG_JPA_UUID);
    Optional<Tag> databaseTag = tagRepository.findByUuid(TAG_DATABASE_UUID);

    // Then
    assertThat(springBootTag).isPresent();
    assertThat(springBootTag.get().getName()).isEqualTo(TestDataSamples.TAG_SPRING_BOOT);

    assertThat(jpaTag).isPresent();
    assertThat(jpaTag.get().getName()).isEqualTo(TAG_JPA);

    assertThat(databaseTag).isPresent();
    assertThat(databaseTag.get().getName()).isEqualTo(TAG_DATABASE);
  }

  @Test
  void shouldFindTagsByName() {
    // When
    Optional<Tag> programmingTag = tagRepository.findByName(TAG_PROGRAMMING);
    Optional<Tag> webDevelopmentTag = tagRepository.findByName(TAG_WEB_DEVELOPMENT);
    Optional<Tag> bestPracticesTag = tagRepository.findByName(TAG_BEST_PRACTICES);

    // Then
    assertThat(programmingTag).isPresent();
    assertThat(programmingTag.get().getUuid()).isEqualTo(TAG_PROGRAMMING_UUID);

    assertThat(webDevelopmentTag).isPresent();
    assertThat(webDevelopmentTag.get().getUuid()).isEqualTo(TAG_WEB_DEVELOPMENT_UUID);

    assertThat(bestPracticesTag).isPresent();
    assertThat(bestPracticesTag.get().getUuid()).isEqualTo(TAG_BEST_PRACTICES_UUID);
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

  @Test
  void shouldPreventDeletionOfTagAssignedToPosts() {
    // Given - Use the "java" tag which is assigned to posts in the migration data
    Optional<Tag> javaTag = tagRepository.findByName(TestDataSamples.TAG_JAVA);
    assertThat(javaTag).isPresent();

    // When & Then - Should throw exception when trying to delete
    assertThatThrownBy(() -> tagRepository.deleteByUuid(javaTag.get().getUuid()))
        .isInstanceOf(TagInUseException.class)
        .hasMessageContaining("Cannot delete tag '" + TestDataSamples.TAG_JAVA + "' because it is assigned to posts");
  }

  @Test
  void shouldFindAllPredefinedTags() {
    // When & Then - Verify all predefined tags exist
    assertThat(tagRepository.findByName(TAG_TECHNOLOGY)).isPresent();
    assertThat(tagRepository.findByName(TAG_PROGRAMMING)).isPresent();
    assertThat(tagRepository.findByName(TestDataSamples.TAG_JAVA)).isPresent();
    assertThat(tagRepository.findByName(TestDataSamples.TAG_SPRING_BOOT)).isPresent();
    assertThat(tagRepository.findByName(TAG_JPA)).isPresent();
    assertThat(tagRepository.findByName(TAG_DATABASE)).isPresent();
    assertThat(tagRepository.findByName(TAG_WEB_DEVELOPMENT)).isPresent();
    assertThat(tagRepository.findByName("tutorial")).isPresent();
    assertThat(tagRepository.findByName(TAG_BEST_PRACTICES)).isPresent();
    assertThat(tagRepository.findByName("architecture")).isPresent();
    assertThat(tagRepository.findByName("microservices")).isPresent();
    assertThat(tagRepository.findByName("testing")).isPresent();
    assertThat(tagRepository.findByName("devops")).isPresent();
    assertThat(tagRepository.findByName("api")).isPresent();
    assertThat(tagRepository.findByName("security")).isPresent();
  }
}
