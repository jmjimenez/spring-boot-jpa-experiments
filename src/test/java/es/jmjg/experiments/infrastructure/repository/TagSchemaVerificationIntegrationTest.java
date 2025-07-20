package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.shared.BaseJpaIntegration;

public class TagSchemaVerificationIntegrationTest extends BaseJpaIntegration {

  @Autowired
  private TagRepository tagRepository;

  @Test
  void shouldCreateAndSaveTagWithCorrectSchema() {
    // Given
    Tag tag = new Tag();
    tag.setUuid(UUID.randomUUID());
    tag.setName("test-tag-fix");

    // When
    Tag savedTag = tagRepository.save(tag);

    // Then
    assertThat(savedTag).isNotNull();
    assertThat(savedTag.getId()).isNotNull();
    assertThat(savedTag.getName()).isEqualTo("test-tag-fix");
    assertThat(savedTag.getUuid()).isNotNull();

    // Verify we can retrieve it
    var foundTag = tagRepository.findById(savedTag.getId());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo("test-tag-fix");
  }

  @Test
  void shouldFindTagByNameAfterSchemaFix() {
    // Given
    Tag tag = new Tag();
    tag.setUuid(UUID.randomUUID());
    tag.setName("schema-fix-verification");
    tagRepository.save(tag);

    // When
    var foundTag = tagRepository.findByName("schema-fix-verification");

    // Then
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo("schema-fix-verification");
  }
}