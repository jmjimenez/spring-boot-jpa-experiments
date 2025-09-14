package es.jmjg.experiments.infrastructure.repository;

import static es.jmjg.experiments.shared.TestDataSamples.*;
import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.domain.tag.exception.TagInUseException;
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
    Optional<Tag> foundTag = tagRepository.findByName(TECHNOLOGY_TAG_NAME);

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(TECHNOLOGY_TAG_NAME);
    assertThat(foundTag.get().getUuid()).isEqualTo(TECHNOLOGY_UUID);
  }

  @Test
  void shouldFindTagByUuid() {
    // When
    Optional<Tag> foundTag = tagRepository.findByUuid(JAVA_UUID);

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getUuid()).isEqualTo(JAVA_UUID);
    assertThat(foundTag.get().getName()).isEqualTo(JAVA_TAG_NAME);
  }

  @Test
  void shouldFindMultipleTagsByUuid() {
    // When
    Optional<Tag> springBootTag = tagRepository.findByUuid(SPRING_BOOT_UUID);
    Optional<Tag> jpaTag = tagRepository.findByUuid(JPA_UUID);
    Optional<Tag> databaseTag = tagRepository.findByUuid(DATABASE_UUID);

    // Then
    assertThat(springBootTag).isPresent();
    assertThat(springBootTag.get().getName()).isEqualTo(SPRING_BOOT_TAG_NAME);

    assertThat(jpaTag).isPresent();
    assertThat(jpaTag.get().getName()).isEqualTo(JPA_TAG_NAME);

    assertThat(databaseTag).isPresent();
    assertThat(databaseTag.get().getName()).isEqualTo(DATABASE_TAG_NAME);
  }

  @Test
  void shouldFindTagsByName() {
    // When
    Optional<Tag> programmingTag = tagRepository.findByName(PROGRAMMING_TAG_NAME);
    Optional<Tag> webDevelopmentTag = tagRepository.findByName(WEB_DEVELOPMENT_TAG_NAME);
    Optional<Tag> bestPracticesTag = tagRepository.findByName(BEST_PRACTICES_TAG_NAME);

    // Then
    assertThat(programmingTag).isPresent();
    assertThat(programmingTag.get().getUuid()).isEqualTo(PROGRAMMING_UUID);

    assertThat(webDevelopmentTag).isPresent();
    assertThat(webDevelopmentTag.get().getUuid()).isEqualTo(WEB_DEVELOPMENT_UUID);

    assertThat(bestPracticesTag).isPresent();
    assertThat(bestPracticesTag.get().getUuid()).isEqualTo(BEST_PRACTICES_UUID);
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
    Optional<Tag> javaTag = tagRepository.findByName(JAVA_TAG_NAME);
    assertThat(javaTag).isPresent();

    // When & Then - Should throw exception when trying to delete
    assertThatThrownBy(() -> tagRepository.deleteByUuid(javaTag.get().getUuid()))
        .isInstanceOf(TagInUseException.class)
        .hasMessageContaining("Cannot delete tag '" + JAVA_TAG_NAME + "' because it is assigned to posts");
  }

  @Test
  void shouldFindAllPredefinedTags() {
    // When & Then - Verify all predefined tags exist
    assertThat(tagRepository.findByName(TECHNOLOGY_TAG_NAME)).isPresent();
    assertThat(tagRepository.findByName(PROGRAMMING_TAG_NAME)).isPresent();
    assertThat(tagRepository.findByName(JAVA_TAG_NAME)).isPresent();
    assertThat(tagRepository.findByName(SPRING_BOOT_TAG_NAME)).isPresent();
    assertThat(tagRepository.findByName(JPA_TAG_NAME)).isPresent();
    assertThat(tagRepository.findByName(DATABASE_TAG_NAME)).isPresent();
    assertThat(tagRepository.findByName(WEB_DEVELOPMENT_TAG_NAME)).isPresent();
    assertThat(tagRepository.findByName("tutorial")).isPresent();
    assertThat(tagRepository.findByName(BEST_PRACTICES_TAG_NAME)).isPresent();
    assertThat(tagRepository.findByName("architecture")).isPresent();
    assertThat(tagRepository.findByName("microservices")).isPresent();
    assertThat(tagRepository.findByName("testing")).isPresent();
    assertThat(tagRepository.findByName("devops")).isPresent();
    assertThat(tagRepository.findByName("api")).isPresent();
    assertThat(tagRepository.findByName("security")).isPresent();
  }
}
