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

import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.tag.repository.TagRepository;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindUsersByTagTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private TagRepository tagRepository;

  private FindUsersByTag findUsersByTag;

  @BeforeEach
  void setUp() {
    findUsersByTag = new FindUsersByTag(userRepository, tagRepository);
  }

  @Test
  void findByTagUuid_WhenTagExists_ShouldReturnUsers() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    User user1 = UserFactory.createBasicUser();
    User user2 = UserFactory.createUser("Another User", "another@example.com", "another_user");
    List<User> expectedUsers = Arrays.asList(user1, user2);

    when(tagRepository.findByUuid(tag.getUuid())).thenReturn(Optional.of(tag));
    when(userRepository.findByTagId(tag.getId())).thenReturn(expectedUsers);

    // When
    List<User> result = findUsersByTag.findByTagUuid(tag.getUuid());

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(user1, user2);
  }

  @Test
  void findByTagUuid_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    UUID tagUuid = UUID.randomUUID();
    when(tagRepository.findByUuid(tagUuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> findUsersByTag.findByTagUuid(tagUuid))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with uuid: " + tagUuid);
  }

  @Test
  void findByTagName_WhenTagExists_ShouldReturnUsers() {
    // Given
    Tag tag = TagFactory.createBasicTag();
    User user1 = UserFactory.createBasicUser();
    List<User> expectedUsers = List.of(user1);

    when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
    when(userRepository.findByTagId(tag.getId())).thenReturn(expectedUsers);

    // When
    List<User> result = findUsersByTag.findByTagName(tag.getName());

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).contains(user1);
  }

  @Test
  void findByTagName_WhenTagDoesNotExist_ShouldThrowTagNotFound() {
    // Given
    String tagName = "nonexistent";
    when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> findUsersByTag.findByTagName(tagName))
        .isInstanceOf(TagNotFound.class)
        .hasMessage("Tag not found with name: " + tagName);
  }

  @Test
  void findByTagName_WhenTagNameIsNull_ShouldReturnEmptyList() {
    // When
    List<User> result = findUsersByTag.findByTagName(null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsEmpty_ShouldReturnEmptyList() {
    // When
    List<User> result = findUsersByTag.findByTagName("");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsWhitespace_ShouldReturnEmptyList() {
    // When
    List<User> result = findUsersByTag.findByTagName("   ");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findByTagName_WhenTagNameIsTrimmed_ShouldWorkCorrectly() {
    // Given
    int tagId = 1;
    Tag tag = TagFactory.createBasicTag(tagId);
    User user1 = UserFactory.createBasicUser();
    List<User> expectedUsers = List.of(user1);

    when(tagRepository.findByName(tag.getName())).thenReturn(Optional.of(tag));
    when(userRepository.findByTagId(tag.getId())).thenReturn(expectedUsers);

    // When
    List<User> result = findUsersByTag.findByTagName(tag.getName());

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).contains(user1);
  }
}
