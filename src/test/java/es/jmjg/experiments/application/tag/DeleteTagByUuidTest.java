package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.tag.exception.TagInUseException;
import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.repository.TagRepository;

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
    doNothing().when(tagRepository).deleteByUuid(uuid);

    // When
    deleteTagByUuid.deleteByUuid(uuid);

    // Then
    verify(tagRepository).deleteByUuid(uuid);
  }

  @Test
  void deleteByUuid_WhenTagDoesNotExist_ShouldPropagateTagNotFound() {
    // Given
    UUID uuid = UUID.randomUUID();
    doThrow(new TagNotFound(uuid)).when(tagRepository).deleteByUuid(uuid);

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(uuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + uuid);
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInPosts_ShouldPropagateTagInUseException() {
    // Given
    UUID uuid = UUID.randomUUID();
    doThrow(new TagInUseException("Cannot delete tag 'test-tag' because it is assigned to posts"))
        .when(tagRepository).deleteByUuid(uuid);

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(uuid))
        .isInstanceOf(TagInUseException.class)
        .hasMessage("Cannot delete tag 'test-tag' because it is assigned to posts");
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInUsers_ShouldPropagateTagInUseException() {
    // Given
    UUID uuid = UUID.randomUUID();
    doThrow(new TagInUseException("Cannot delete tag 'test-tag' because it is assigned to users"))
        .when(tagRepository).deleteByUuid(uuid);

    // When & Then
    assertThatThrownBy(() -> deleteTagByUuid.deleteByUuid(uuid))
        .isInstanceOf(TagInUseException.class)
        .hasMessage("Cannot delete tag 'test-tag' because it is assigned to users");
  }
}