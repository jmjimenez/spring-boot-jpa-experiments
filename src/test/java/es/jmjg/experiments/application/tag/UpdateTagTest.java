package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import es.jmjg.experiments.shared.UserFactory;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.tag.repository.TagRepository;
import es.jmjg.experiments.shared.TagFactory;

@ExtendWith(MockitoExtension.class)
class UpdateTagTest {

  @Mock
  private TagRepository tagRepository;

  private UpdateTag updateTag;

  @BeforeEach
  void setUp() {
    updateTag = new UpdateTag(tagRepository);
  }

  @Test
  void updateName_WhenTagExists_ShouldUpdateAndReturnTag() {
    // Given
    String updatedName = "updated-tag";
    Tag tag = TagFactory.createBasicTag();

    when(tagRepository.findByUuid(tag.getUuid())).thenReturn(Optional.of(tag));
    when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Tag result = updateTag.update(TagFactory.createUpdateTagDto(tag.getUuid(), updatedName, UserFactory.createAdminUser()));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUuid()).isEqualTo(tag.getUuid());
    assertThat(result.getName()).isEqualTo(updatedName);
  }

  @Test
  void updateName_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID uuid = UUID.randomUUID();
    String newName = "updated-tag";

    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(uuid, newName,UserFactory.createAdminUser())))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with id: " + uuid);
  }

  @Test
  void updateName_WhenNewNameIsEmpty_ShouldThrowIllegalArgumentException() {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(uuid, "",UserFactory.createAdminUser())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsWhitespace_ShouldThrowIllegalArgumentException() {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> updateTag.update(TagFactory.createUpdateTagDto(uuid, "   ",UserFactory.createAdminUser())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tag name cannot be null or empty");
  }

  @Test
  void updateName_WhenNewNameIsTrimmed_ShouldWorkCorrectly() {
    // Given
    String updatedName = "  updated-tag  ";
    Tag tag = TagFactory.createBasicTag();

    when(tagRepository.findByUuid(tag.getUuid())).thenReturn(Optional.of(tag));
    when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Tag result = updateTag.update(TagFactory.createUpdateTagDto(tag.getUuid(), updatedName,UserFactory.createAdminUser()));

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUuid()).isEqualTo(tag.getUuid());
    assertThat(result.getName()).isEqualTo(updatedName.trim());
  }
}
