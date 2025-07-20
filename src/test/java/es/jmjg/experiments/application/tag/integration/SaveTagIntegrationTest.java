package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.SaveTag;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TagFactory;

class SaveTagIntegrationTest extends BaseIntegration {

  @Autowired
  private SaveTag saveTag;

  @Autowired
  private TagRepository tagRepository;

  @Test
  void save_ShouldSaveAndReturnTag() {
    // Given
    Tag tag = TagFactory.createBasicTag();

    // When
    Tag savedTag = saveTag.save(tag);

    // Then
    assertThat(savedTag).isNotNull();
    assertThat(savedTag.getId()).isNotNull();
    assertThat(savedTag.getName()).isEqualTo(tag.getName());
    assertThat(savedTag.getUuid()).isNotNull();

    // Verify we can retrieve it from the database
    var foundTag = tagRepository.findById(savedTag.getId());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(tag.getName());
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
    assertThat(savedTag.getName()).isEqualTo(tag.getName());
    assertThat(savedTag.getUuid()).isNotNull();

    // Verify we can retrieve it from the database
    var foundTag = tagRepository.findById(savedTag.getId());
    assertThat(foundTag).isPresent();
    assertThat(foundTag.get().getName()).isEqualTo(tag.getName());
  }
}