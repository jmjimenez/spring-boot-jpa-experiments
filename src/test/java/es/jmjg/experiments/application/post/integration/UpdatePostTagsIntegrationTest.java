package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import es.jmjg.experiments.application.post.UpdatePostTags;
import es.jmjg.experiments.application.post.dto.UpdatePostTagsDto;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class UpdatePostTagsIntegrationTest extends BaseIntegration {

  @Autowired
  private UpdatePostTags updatePostTags;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  @Transactional
  void updateTags_shouldUpdateTagsSuccessfully() {
    // Given
    User owner = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    Post post = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow();
    var authenticatedUser = AuthenticatedUserFactory.createAuthenticatedUserDto(owner);
    List<String> newTags = List.of(TestDataSamples.TECHNOLOGY_TAG_NAME, TestDataSamples.JAVA_TAG_NAME);
    UpdatePostTagsDto dto = new UpdatePostTagsDto(post.getUuid(), newTags, authenticatedUser);

    // When
    Post updatedPost = updatePostTags.update(dto);

    // Then
    assertThat(updatedPost.getTags()).hasSize(2);
    assertThat(updatedPost.getTags()).extracting(Tag::getName).containsExactlyInAnyOrderElementsOf(newTags);

    // Also verify persisted state
    Post persisted = postRepository.findByUuid(post.getUuid()).orElseThrow();
    assertThat(persisted.getTags()).hasSize(2);
    assertThat(persisted.getTags()).extracting(Tag::getName).containsExactlyInAnyOrderElementsOf(newTags);
  }


  @Test
  @Transactional
  void updateTags_WhenTagListIsEmpty_shouldUpdateTagsSuccessfully() {
    // Given
    User owner = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    Post post = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow();
    var authenticatedUser = AuthenticatedUserFactory.createAuthenticatedUserDto(owner);
    List<String> newTags = List.of();
    UpdatePostTagsDto dto = new UpdatePostTagsDto(post.getUuid(), newTags, authenticatedUser);

    // When
    Post updatedPost = updatePostTags.update(dto);

    // Then
    assertThat(updatedPost.getTags()).hasSize(0);

    // Also verify persisted state
    Post persisted = postRepository.findByUuid(post.getUuid()).orElseThrow();
    assertThat(persisted.getTags()).hasSize(0);
  }

  @Test
  @Transactional
  void updateTags_shouldThrowTagNotFound() {
    // Given
    User owner = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    Post post = postRepository.findByUuid(TestDataSamples.POST_1_UUID).orElseThrow();
    var authenticatedUser = AuthenticatedUserFactory.createAuthenticatedUserDto(owner);
    List<String> newTags = List.of(TestDataSamples.TECHNOLOGY_TAG_NAME, "notfound");
    UpdatePostTagsDto dto = new UpdatePostTagsDto(post.getUuid(), newTags, authenticatedUser);

    // When / Then
    assertThatThrownBy(() -> updatePostTags.update(dto))
      .isInstanceOf(TagNotFound.class)
      .hasMessageContaining("Tag not found");
  }
}
