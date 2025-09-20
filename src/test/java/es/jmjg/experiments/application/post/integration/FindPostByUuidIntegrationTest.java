package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.tag.repository.TagRepository;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import es.jmjg.experiments.domain.post.exception.PostNotFound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.FindPostByUuid;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import org.springframework.transaction.annotation.Transactional;

class FindPostByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindPostByUuid findPostByUuid;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Autowired
  private TagRepository tagRepository;

  @Test
  void findByUuid_WhenPostExists_ShouldReturnPost() {
    // Given
    Tag tagTechnology = tagRepository.findByName(TestDataSamples.TECHNOLOGY_TAG_NAME).orElseThrow(() -> new RuntimeException("tag not found"));
    Tag tagJava = tagRepository.findByName(TestDataSamples.TAG_JAVA).orElseThrow(() -> new RuntimeException("tag not found"));
    List<Tag> tags = Arrays.asList(tagTechnology, tagJava);

    User testUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow(() -> new RuntimeException("user not found"));
    Post testPost = PostFactory.createBasicPost(testUser);
    testPost.setTags(tags);
    postRepository.save(testPost);

    User anotherUser = userRepository.findByUuid(TestDataSamples.PATRICIA_UUID).orElseThrow(() -> new RuntimeException("user not found"));
    PostComment comment1 = PostFactory.createPostComment(anotherUser, testPost, "comment 1");
    testPost.getComments().add(comment1);
    postRepository.save(testPost);

    // When
    Post result = findPostByUuid.findByUuid(testPost.getUuid());

    // Then
    assertThat(result.getBody()).isEqualTo(testPost.getBody());
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());
    assertThat(result.getUuid()).isEqualTo(testPost.getUuid());
    assertThat(result.getTitle()).isEqualTo(testPost.getTitle());
    assertThat(result.getTags()).hasSize(2);
    assertThat(result.getTags()).extracting("name").containsExactlyInAnyOrder(
        TestDataSamples.TECHNOLOGY_TAG_NAME, TestDataSamples.TAG_JAVA);
    assertThat(result.getComments()).hasSize(1);
    assertThat(result.getComments().getFirst().getComment()).isEqualTo("comment 1");
  }

  @Test
  void findByUuid_WhenPostDoesNotExist_ShouldThrowPostNotFoundException() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();

    // When
    assertThatThrownBy(() -> findPostByUuid.findByUuid(nonExistentUuid))
        .isInstanceOf(PostNotFound.class)
        .hasMessage("Post with UUID " + nonExistentUuid + " not found");
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldThrowIllegalArgumentException() {
    // When
    assertThatThrownBy(() -> findPostByUuid.findByUuid(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UUID cannot be null");
  }
}
