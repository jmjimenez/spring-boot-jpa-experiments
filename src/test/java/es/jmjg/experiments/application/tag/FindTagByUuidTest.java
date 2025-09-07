package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
class FindTagByUuidTest {

  @Mock
  private TagRepository tagRepository;

  private FindTagByUuid findTagByUuid;

  @BeforeEach
  void setUp() {
    findTagByUuid = new FindTagByUuid(tagRepository);
  }

  @Test
  void findByUuid_WhenTagExists_ShouldReturnTag() {
    // Given
    UUID uuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(uuid, "test-tag");
    tag.setId(1);

    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.of(tag));

    // When
    Tag result = findTagByUuid.findByUuid(uuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("test-tag");
    assertThat(result.getUuid()).isEqualTo(uuid);
  }

  @Test
  void findByUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID uuid = UUID.randomUUID();
    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> findTagByUuid.findByUuid(uuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + uuid);
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldThrowIllegalArgumentException() {
    // When & Then
    assertThatThrownBy(() -> findTagByUuid.findByUuid(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UUID cannot be null");
  }

  @Test
  void findByUuidOptional_WhenTagExists_ShouldReturnOptionalWithTag() {
    // Given
    UUID uuid = UUID.randomUUID();
    Tag tag = TagFactory.createTag(uuid, "test-tag");
    tag.setId(1);

    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.of(tag));

    // When
    Optional<Tag> result = findTagByUuid.findByUuidOptional(uuid);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(1);
    assertThat(result.get().getName()).isEqualTo("test-tag");
    assertThat(result.get().getUuid()).isEqualTo(uuid);
  }

  @Test
  void findByUuidOptional_WhenTagDoesNotExist_ShouldReturnEmptyOptional() {
    // Given
    UUID uuid = UUID.randomUUID();
    when(tagRepository.findByUuid(uuid)).thenReturn(Optional.empty());

    // When
    Optional<Tag> result = findTagByUuid.findByUuidOptional(uuid);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuidOptional_WhenUuidIsNull_ShouldReturnEmptyOptional() {
    // When
    Optional<Tag> result = findTagByUuid.findByUuidOptional(null);

    // Then
    assertThat(result).isEmpty();
  }
}
