package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import es.jmjg.experiments.domain.tag.entity.TagUuid;
import java.util.Optional;

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
    var uuid = TagUuid.randomUUID();
    Tag tag = TagFactory.createTag(uuid, "test-tag");
    tag.setId(1);

    when(tagRepository.findByTagUuid(uuid)).thenReturn(Optional.of(tag));

    // When
    Tag result = findTagByUuid.findByTagUuid(uuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("test-tag");
    assertThat(result.getTagUuid()).isEqualTo(uuid);
  }

  @Test
  void findByUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    var uuid = TagUuid.randomUUID();
    when(tagRepository.findByTagUuid(uuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> findTagByUuid.findByTagUuid(uuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with id: " + uuid);
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldThrowIllegalArgumentException() {
    // When & Then
    assertThatThrownBy(() -> findTagByUuid.findByTagUuid(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UUID cannot be null");
  }
}
