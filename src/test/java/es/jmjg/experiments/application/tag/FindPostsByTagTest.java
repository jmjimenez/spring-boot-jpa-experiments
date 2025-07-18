package es.jmjg.experiments.application.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.TagRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindPostsByTagTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private TagRepository tagRepository;

  private FindPostsByTag findPostsByTag;

  @BeforeEach
  void setUp() {
    findPostsByTag = new FindPostsByTag(postRepository, tagRepository);
  }

  @Test
  void findByTagUuid_WhenTagExists_ShouldReturnPosts() {
    // Given
    UUID tagUuid = UUID.randomUUID();
    Tag tag = TagFactory.createJavaTag();
    tag.setId(1);
    User user = UserFactory.createBasicUser();
    Post post1 = PostFactory.createBasicPost(user);
    Post post2 = PostFactory.createPost(user, "Another Post", "Another Body");
    List<Post> expectedPosts = Arrays.asList(post1, post2);

    when(tagRepository.findByUuid(tagUuid)).thenReturn(Optional.of(tag));
    when(postRepository.findByTagId(1)).thenReturn(expectedPosts);

    // When
    List<Post> result = findPostsByTag.findByTagUuid(tagUuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(post1, post2);
  }

  @Test
  void findByTagUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID tagUuid = UUID.randomUUID();
    when(tagRepository.findByUuid(tagUuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> findPostsByTag.findByTagUuid(tagUuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + tagUuid);
  }

  @Test
  void findByTagName_WhenTagExists_ShouldReturnPosts() {
    // Given
    String tagName = "java";
    Tag tag = TagFactory.createJavaTag();
    tag.setId(1);
    User user = UserFactory.createBasicUser();
    Post post1 = PostFactory.createBasicPost(user);
    List<Post> expectedPosts = List.of(post1);

    when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));
    when(postRepository.findByTagId(1)).thenReturn(expectedPosts);

    // When
    List<Post> result = findPostsByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).contains(post1);
  }

  @Test
  void findByTagName_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    String tagName = "nonexistent";
    when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> findPostsByTag.findByTagName(tagName))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with name: " + tagName);
  }

  @Test
  void findByTagName_WhenTagNameIsNull_ShouldReturnEmptyList() {
    // When
    List<Post> result = findPostsByTag.findByTagName(null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsEmpty_ShouldReturnEmptyList() {
    // When
    List<Post> result = findPostsByTag.findByTagName("");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsWhitespace_ShouldReturnEmptyList() {
    // When
    List<Post> result = findPostsByTag.findByTagName("   ");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsTrimmed_ShouldWorkCorrectly() {
    // Given
    String tagName = "  java  ";
    String expectedTagName = "java";
    Tag tag = TagFactory.createJavaTag();
    tag.setId(1);
    User user = UserFactory.createBasicUser();
    Post post1 = PostFactory.createBasicPost(user);
    List<Post> expectedPosts = List.of(post1);

    when(tagRepository.findByName(expectedTagName)).thenReturn(Optional.of(tag));
    when(postRepository.findByTagId(1)).thenReturn(expectedPosts);

    // When
    List<Post> result = findPostsByTag.findByTagName(tagName);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).contains(post1);
  }
}