package es.jmjg.experiments.application.tag.integration;

import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.tag.SaveTag;
import es.jmjg.experiments.application.tag.exception.TagAlreadyExistsException;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.TagRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TagFactory;

class SaveTagIntegrationTest extends BaseIntegration {

  @Autowired
  private SaveTag saveTag;

  @Autowired
  private TagRepositoryImpl tagRepository;

  @Test
  void save_ShouldSaveAndReturnTag() {
    // Given
    var adminUser = UserFactory.createAdminUser();
    Tag tag = TagFactory.createTag("basic-save-test");

    // When
    Tag savedTag = saveTag.save(TagFactory.createSaveTagDto(tag.getUuid(), tag.getName(), adminUser));

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
    var adminUser = UserFactory.createAdminUser();
    Tag tag = TagFactory.createTag("custom-tag");

    // When
    Tag savedTag = saveTag.save(TagFactory.createSaveTagDto(tag.getUuid(), tag.getName(), adminUser));

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
    Tag tag = TagFactory.createTag("different-name");
    tag.setUuid(TestDataSamples.TECHNOLOGY_UUID); // Use the same UUID

    // When & Then
    assertThatThrownBy(() -> saveTag.save(TagFactory.createSaveTagDto(tag.getUuid(), tag.getName(), UserFactory.createAdminUser())))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag with uuid '" + tag.getUuid() + "' already exists");
  }

  @Test
  void save_WhenDuplicateName_ShouldThrowTagAlreadyExistsException() {
    // Given - using existing sample data from migration test
    Tag tag = TagFactory.createTag(TestDataSamples.TECHNOLOGY_TAG_NAME);

    // When & Then
    assertThatThrownBy(() -> saveTag.save(TagFactory.createSaveTagDto(tag.getUuid(), tag.getName(), UserFactory.createAdminUser())))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag with name '" + tag.getName() + "' already exists with uuid '" + TestDataSamples.TECHNOLOGY_UUID + "'");
  }
}
