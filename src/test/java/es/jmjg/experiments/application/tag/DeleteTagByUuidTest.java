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

import es.jmjg.experiments.application.tag.exception.TagInUseException;
import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.TagFactory;

@ExtendWith(MockitoExtension.class)
class DeleteTagByUuidTest {

  @Mock
  private TagRepository tagRepository;

  private DeleteTagByUuid deleteTagByUuid;

  @BeforeEach
  void setUp() {
    deleteTagByUuid = new DeleteTagByUuid(tagRepository);
  }

  @Test
  void deleteByUuid_WhenTagExistsAndNotInUse_ShouldDeleteTag() {
    // Given
    UUID uuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(uuid, "test-tag");
    tag.setId(1);

    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.of(tag));
    when(tagRepository.isTagUsedInPosts(1)).thenReturn(false);
    when(tagRepository.isTagUsedInUsers(1)).thenReturn(false);

    // When
    deleteTagByUuid.deleteByUuid(uuid);

    // Then
    verify(tagRepository).deleteByUuid(uuid);
  }

  @Test
  void deleteByUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID uuid = UUID.randomUUID();
    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(uuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + uuid);

    verify(tagRepository, never()).deleteByUuid(any());
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInPosts_ShouldThrowTagInUseException() {
    // Given
    UUID uuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(uuid, "test-tag");
    tag.setId(1);

    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.of(tag));
    when(tagRepository.isTagUsedInPosts(1)).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(uuid))
        .isInstanceOf(TagInUseException.class)
        .hasMessage("Cannot delete tag 'test-tag' with uuid: " + uuid + " because it is currently in use");

    verify(tagRepository, never()).deleteByUuid(any());
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInUsers_ShouldThrowTagInUseException() {
    // Given
    UUID uuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(uuid, "test-tag");
    tag.setId(1);

    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.of(tag));
    when(tagRepository.isTagUsedInPosts(1)).thenReturn(false);
    when(tagRepository.isTagUsedInUsers(1)).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(uuid))
        .isInstanceOf(TagInUseException.class)
        .hasMessage("Cannot delete tag 'test-tag' with uuid: " + uuid + " because it is currently in use");

    verify(tagRepository, never()).deleteByUuid(any());
  }
}