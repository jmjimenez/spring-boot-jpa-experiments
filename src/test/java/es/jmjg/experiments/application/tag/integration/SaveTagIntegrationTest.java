package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.SaveTag;
import es.jmjg.experiments.application.tag.exception.TagAlreadyExistsException;
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
    Tag tag = TagFactory.createTag("basic-save-test");

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
  void save_WhenDuplicateUuid_ShouldThrowTagAlreadyExistsException() {
    // Given
    Tag tag1 = TagFactory.createTag("duplicate-uuid-test-1");
    Tag savedTag1 = tagRepository.save(tag1);

    Tag tag2 = TagFactory.createTag("different-name");
    tag2.setUuid(savedTag1.getUuid()); // Use the same UUID

    // When & Then
    assertThatThrownBy(() -> saveTag.save(tag2))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag with uuid '" + savedTag1.getUuid() + "' already exists");
  }

  @Test
  void save_WhenDuplicateName_ShouldThrowTagAlreadyExistsException() {
    // Given - using existing sample data from migration test
    final String technologyTagName = "technology";
    final String technologyTagUuid = "550e8400-e29b-41d4-a716-446655440056";
    Tag tag2 = TagFactory.createTag(technologyTagName);

    // When & Then
    assertThatThrownBy(() -> saveTag.save(tag2))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag with name '" + technologyTagName + "' already exists with uuid '" + technologyTagUuid + "'");
  }
}