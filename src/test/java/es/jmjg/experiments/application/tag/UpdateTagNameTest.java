package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.TagFactory;

@ExtendWith(MockitoExtension.class)
class UpdateTagNameTest {

  @Mock
  private TagRepository tagRepository;

  private UpdateTagName updateTagName;

  @BeforeEach
  void setUp() {
    updateTagName = new UpdateTagName(tagRepository);
  }

  @Test
  void updateName_WhenTagExists_ShouldUpdateAndReturnTag() {
    // Given
    UUID uuid = UUID.randomUUID();
    String newName = "updated-tag";
    Tag tag = TagFactory.createTag(uuid, "old-tag");
    tag.setId(1);
    Tag updatedTag = TagFactory.createTag(uuid, newName);
    updatedTag.setId(1);

    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.of(tag));
    when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);

    // When
    Tag result = updateTagName.updateName(uuid, newName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(newName);
    assertThat(result.getUuid()).isEqualTo(uuid);
    assertThat(result.getId()).isEqualTo(1);
  }

  @Test
  void updateName_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID uuid = UUID.randomUUID();
    String newName = "updated-tag";

    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> updateTagName.updateName(uuid, newName))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + uuid);
  }

  @Test
  void updateName_WhenNewNameIsNull_ShouldThrowIllegalArgumentException() {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> updateTagName.updateName(uuid, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsEmpty_ShouldThrowIllegalArgumentException() {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> updateTagName.updateName(uuid, ""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsWhitespace_ShouldThrowIllegalArgumentException() {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> updateTagName.updateName(uuid, "   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsTrimmed_ShouldWorkCorrectly() {
    // Given
    UUID uuid = UUID.randomUUID();
    String newName = "  updated-tag  ";
    String expectedName = "updated-tag";
    Tag tag = TagFactory.createTag(uuid, "old-tag");
    tag.setId(1);
    Tag updatedTag = TagFactory.createTag(uuid, expectedName);
    updatedTag.setId(1);

    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.of(tag));
    when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);

    // When
    Tag result = updateTagName.updateName(uuid, newName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(expectedName);
    assertThat(result.getUuid()).isEqualTo(uuid);
  }
}