package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.TagFactory;
import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
class FindTagByPatternTest {

  @Mock
  private TagRepository tagRepository;

  private FindTagByPattern findTagByPattern;

  @BeforeEach
  void setUp() {
    findTagByPattern = new FindTagByPattern(tagRepository);
  }

  @Test
  @Transactional
  void findByPattern_WhenPatternMatches_ShouldReturnMatchingTags() {
    // Given
    String pattern = "java";
    Tag javaTag = TagFactory.createJavaTag();
    Tag springBootTag = TagFactory.createSpringBootTag();
    List<Tag> expectedTags = Arrays.asList(javaTag, springBootTag);

    when(tagRepository.findByNameContainingPattern(pattern)).thenReturn(expectedTags);

    // When
    List<Tag> result = findTagByPattern.findByPattern(pattern);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(javaTag, springBootTag);
  }

  @Test
  void findByPattern_WhenPatternIsNull_ShouldReturnEmptyList() {
    // When
    List<Tag> result = findTagByPattern.findByPattern(null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByPattern_WhenPatternIsEmpty_ShouldReturnEmptyList() {
    // When
    List<Tag> result = findTagByPattern.findByPattern("");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByPattern_WhenPatternIsWhitespace_ShouldReturnEmptyList() {
    // When
    List<Tag> result = findTagByPattern.findByPattern("   ");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByPattern_WhenNoMatches_ShouldReturnEmptyList() {
    // Given
    String pattern = "nonexistent";
    when(tagRepository.findByNameContainingPattern(pattern)).thenReturn(List.of());

    // When
    List<Tag> result = findTagByPattern.findByPattern(pattern);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByPattern_WhenPatternIsTrimmed_ShouldWorkCorrectly() {
    // Given
    String pattern = "  java  ";
    Tag javaTag = TagFactory.createJavaTag();
    List<Tag> expectedTags = List.of(javaTag);

    when(tagRepository.findByNameContainingPattern("java")).thenReturn(expectedTags);

    // When
    List<Tag> result = findTagByPattern.findByPattern(pattern);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).contains(javaTag);
  }
}