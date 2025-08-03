package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.shared.BaseJpaIntegration;

@Import(TagRepositoryImpl.class)
public class TagRepositoryIntegrationTest extends BaseJpaIntegration {

  @Autowired
  private TagRepositoryImpl tagRepository;

  // Sample tags from Flyway migration
  private static final UUID TECHNOLOGY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440056");
  private static final UUID PROGRAMMING_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440057");
  private static final UUID JAVA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440058");
  private static final UUID SPRING_BOOT_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440059");
  private static final UUID JPA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440060");
  private static final UUID DATABASE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440061");
  private static final UUID WEB_DEVELOPMENT_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440062");
  private static final UUID BEST_PRACTICES_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440064");

  @Test
  void shouldFindTagByName() {
    // When
    Optional<Tag> foundTag = tagRepository.findByName("technology");

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo("technology");
    assertThat(foundTag.get().getUuid()).isEqualTo(TECHNOLOGY_UUID);
  }

  @Test
  void shouldFindTagByUuid() {
    // When
    Optional<Tag> foundTag = tagRepository.findByUuid(JAVA_UUID);

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getUuid()).isEqualTo(JAVA_UUID);
    assertThat(foundTag.get().getName()).isEqualTo("java");
  }

  @Test
  void shouldFindMultipleTagsByUuid() {
    // When
    Optional<Tag> springBootTag = tagRepository.findByUuid(SPRING_BOOT_UUID);
    Optional<Tag> jpaTag = tagRepository.findByUuid(JPA_UUID);
    Optional<Tag> databaseTag = tagRepository.findByUuid(DATABASE_UUID);

    // Then
    assertThat(springBootTag).isPresent();
    assertThat(springBootTag.get().getName()).isEqualTo("spring-boot");

    assertThat(jpaTag).isPresent();
    assertThat(jpaTag.get().getName()).isEqualTo("jpa");

    assertThat(databaseTag).isPresent();
    assertThat(databaseTag.get().getName()).isEqualTo("database");
  }

  @Test
  void shouldFindTagsByName() {
    // When
    Optional<Tag> programmingTag = tagRepository.findByName("programming");
    Optional<Tag> webDevelopmentTag = tagRepository.findByName("web-development");
    Optional<Tag> bestPracticesTag = tagRepository.findByName("best-practices");

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
    Optional<Tag> javaTag = tagRepository.findByName("java");
    assertThat(javaTag).isPresent();

    // When & Then - Should throw exception when trying to delete
    assertThatThrownBy(() -> tagRepository.deleteByUuid(javaTag.get().getUuid()))
        .isInstanceOf(es.jmjg.experiments.domain.exception.TagInUseException.class)
        .hasMessageContaining("Cannot delete tag 'java' because it is assigned to posts");
  }

  @Test
  void shouldFindAllPredefinedTags() {
    // When & Then - Verify all predefined tags exist
    assertThat(tagRepository.findByName("technology")).isPresent();
    assertThat(tagRepository.findByName("programming")).isPresent();
    assertThat(tagRepository.findByName("java")).isPresent();
    assertThat(tagRepository.findByName("spring-boot")).isPresent();
    assertThat(tagRepository.findByName("jpa")).isPresent();
    assertThat(tagRepository.findByName("database")).isPresent();
    assertThat(tagRepository.findByName("web-development")).isPresent();
    assertThat(tagRepository.findByName("tutorial")).isPresent();
    assertThat(tagRepository.findByName("best-practices")).isPresent();
    assertThat(tagRepository.findByName("architecture")).isPresent();
    assertThat(tagRepository.findByName("microservices")).isPresent();
    assertThat(tagRepository.findByName("testing")).isPresent();
    assertThat(tagRepository.findByName("devops")).isPresent();
    assertThat(tagRepository.findByName("api")).isPresent();
    assertThat(tagRepository.findByName("security")).isPresent();
  }
}