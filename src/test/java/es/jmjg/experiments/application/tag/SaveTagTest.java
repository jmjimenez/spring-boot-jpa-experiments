package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import es.jmjg.experiments.shared.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.tag.exception.TagAlreadyExistsException;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.repository.TagRepository;
import es.jmjg.experiments.shared.TagFactory;

@ExtendWith(MockitoExtension.class)
class SaveTagTest {

  @Mock
  private TagRepository tagRepository;

  private SaveTag saveTag;

  @BeforeEach
  void setUp() {
    saveTag = new SaveTag(tagRepository);
  }

  @Test
  void save_ShouldReturnSavedTag() {
    // Given
    var adminUser = UserFactory.createAdminUser();
    int tagId = 1;
    Tag tag = TagFactory.createBasicTag(tagId);

    when(tagRepository.save(any(Tag.class))).thenReturn(tag);

    // When
    Tag result = saveTag.save(TagFactory.createSaveTagDto(tag.getUuid(), tag.getName(), adminUser));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(tagId);
    assertThat(result.getName()).isEqualTo(tag.getName());
    assertThat(result.getUuid()).isEqualTo(tag.getUuid());
  }

  @Test
  void save_WhenDuplicateUuid_ShouldThrowTagAlreadyExistsException() {
    // Given
    var user = UserFactory.createAdminUser();
    Tag tag = TagFactory.createBasicTag();
    when(tagRepository.save(any(Tag.class)))
        .thenThrow(
            new TagAlreadyExistsException("Tag with uuid '" + tag.getUuid() + "' already exists"));

    // When & Then
    var saveTagDto = TagFactory.createSaveTagDto(tag.getUuid(), tag.getName(), user);
    assertThatThrownBy(() -> saveTag.save(saveTagDto))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag with uuid '" + tag.getUuid() + "' already exists");
  }

  @Test
  void save_WhenDuplicateName_ShouldThrowTagAlreadyExistsException() {
    // Given
    var user = UserFactory.createAdminUser();
    Tag tag = TagFactory.createTag("existing-tag");
    when(tagRepository.save(any(Tag.class)))
        .thenThrow(
            new TagAlreadyExistsException("Tag with name '" + tag.getName() + "' and uuid '" + tag.getUuid() + "' already exists"));

    // When & Then
    var saveTagDto = TagFactory.createSaveTagDto(tag.getUuid(), tag.getName(), user);
    assertThatThrownBy(() -> saveTag.save(saveTagDto))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag with name '" + tag.getName() + "' and uuid '" + tag.getUuid() + "' already exists");
  }

  @Test
  void save_WhenGenericDataIntegrityViolation_ShouldThrowTagAlreadyExistsException() {
    // Given
    var user = UserFactory.createAdminUser();
    Tag tag = TagFactory.createBasicTag();
    when(tagRepository.save(any(Tag.class)))
        .thenThrow(new TagAlreadyExistsException("Tag already exists"));

    // When & Then
    var saveTagDto = TagFactory.createSaveTagDto(tag.getUuid(), tag.getName(), user);
    assertThatThrownBy(() -> saveTag.save(saveTagDto))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag already exists");
  }

  @Test
  void save_WhenRepositoryThrowsOtherException_ShouldPropagateException() {
    // Given
    var user = UserFactory.createAdminUser();
    Tag tag = TagFactory.createBasicTag();
    when(tagRepository.save(any(Tag.class)))
        .thenThrow(new RuntimeException("Database error"));

    // When & Then
    var saveTagDto = TagFactory.createSaveTagDto(tag.getUuid(), tag.getName(), user);
    assertThatThrownBy(() -> saveTag.save(saveTagDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
  }
}
