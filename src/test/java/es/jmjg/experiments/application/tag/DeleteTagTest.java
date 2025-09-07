package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.UserFactory;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.domain.tag.exception.TagInUseException;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.tag.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
class DeleteTagTest {

  @Mock
  private TagRepository tagRepository;

  private DeleteTag deleteTag;

  @BeforeEach
  void setUp() {
    deleteTag = new DeleteTag(tagRepository);
  }

  @Test
  void deleteByUuid_WhenTagExistsAndNotInUse_ShouldDeleteTag() {
    // Given
    var adminUser = UserFactory.createAdminUser();
    UUID uuid = UUID.randomUUID();
    doNothing().when(tagRepository).deleteByUuid(uuid);

    // When
    deleteTag.delete(TagFactory.createDeleteTagDto(uuid, adminUser));

    // Then
    verify(tagRepository).deleteByUuid(uuid);
  }

  @Test
  void deleteByUuid_WhenTagDoesNotExist_ShouldPropagateTagNotFound() {
    // Given
    var adminUser = UserFactory.createAdminUser();
    UUID uuid = UUID.randomUUID();
    doThrow(new TagNotFound(uuid)).when(tagRepository).deleteByUuid(uuid);

    // When & Then
    assertThatThrownBy(() -> deleteTag.delete(TagFactory.createDeleteTagDto(uuid, adminUser)))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + uuid);
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInPosts_ShouldPropagateTagInUseException() {
    // Given
    var adminUser = UserFactory.createAdminUser();
    UUID uuid = UUID.randomUUID();
    doThrow(new TagInUseException("Cannot delete tag 'test-tag' because it is assigned to posts"))
        .when(tagRepository).deleteByUuid(uuid);

    // When & Then
    assertThatThrownBy(() -> deleteTag.delete(TagFactory.createDeleteTagDto(uuid, adminUser)))
        .isInstanceOf(TagInUseException.class)
        .hasMessage("Cannot delete tag 'test-tag' because it is assigned to posts");
  }

  @Test
  void deleteByUuid_WhenTagIsUsedInUsers_ShouldPropagateTagInUseException() {
    // Given
    var adminUser = UserFactory.createAdminUser();
    UUID uuid = UUID.randomUUID();
    doThrow(new TagInUseException("Cannot delete tag 'test-tag' because it is assigned to users"))
        .when(tagRepository).deleteByUuid(uuid);

    // When & Then
    assertThatThrownBy(() -> deleteTag.delete(TagFactory.createDeleteTagDto(uuid, adminUser)))
        .isInstanceOf(TagInUseException.class)
        .hasMessage("Cannot delete tag 'test-tag' because it is assigned to users");
  }
}
