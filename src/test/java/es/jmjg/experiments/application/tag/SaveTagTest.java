package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    Tag tag = TagFactory.createBasicTag();
    Tag savedTag = TagFactory.createTag(tag.getUuid(), tag.getName());
    savedTag.setId(1);

    when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

    // When
    Tag result = saveTag.save(tag);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("test-tag");
    assertThat(result.getUuid()).isEqualTo(tag.getUuid());
  }

  @Test
  void save_WithCustomTag_ShouldReturnSavedTag() {
    // Given
    Tag tag = TagFactory.createTag("custom-tag");
    Tag savedTag = TagFactory.createTag(tag.getUuid(), tag.getName());
    savedTag.setId(2);

    when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

    // When
    Tag result = saveTag.save(tag);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getName()).isEqualTo("custom-tag");
    assertThat(result.getUuid()).isEqualTo(tag.getUuid());
  }

  @Test
  void save_WhenDuplicateUuid_ShouldThrowTagAlreadyExistsException() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    when(tagRepository.save(any(Tag.class)))
        .thenThrow(
            new TagAlreadyExistsException("Tag with uuid '" + tag.getUuid() + "' already exists"));

    // When & Then
    assertThatThrownBy(() -> saveTag.save(tag))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag with uuid '" + tag.getUuid() + "' already exists");
  }

  @Test
  void save_WhenDuplicateName_ShouldThrowTagAlreadyExistsException() {
    // Given
    Tag tag = TagFactory.createTag("existing-tag");
    when(tagRepository.save(any(Tag.class)))
        .thenThrow(
            new TagAlreadyExistsException("Tag with name '" + tag.getName() + "' and uuid '" + tag.getUuid() + "' already exists"));

    // When & Then
    assertThatThrownBy(() -> saveTag.save(tag))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag with name '" + tag.getName() + "' and uuid '" + tag.getUuid() + "' already exists");
  }

  @Test
  void save_WhenGenericDataIntegrityViolation_ShouldThrowTagAlreadyExistsException() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    when(tagRepository.save(any(Tag.class)))
        .thenThrow(new TagAlreadyExistsException("Tag already exists"));

    // When & Then
    assertThatThrownBy(() -> saveTag.save(tag))
        .isInstanceOf(TagAlreadyExistsException.class)
        .hasMessage("Tag already exists");
  }

  @Test
  void save_WhenRepositoryThrowsOtherException_ShouldPropagateException() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    when(tagRepository.save(any(Tag.class)))
        .thenThrow(new RuntimeException("Database error"));

    // When & Then
    assertThatThrownBy(() -> saveTag.save(tag))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
  }
}