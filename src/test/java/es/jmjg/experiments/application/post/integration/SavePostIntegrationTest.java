package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.dto.SavePostDto;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.PostRepositoryImpl;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TestDataSamples;

class SavePostIntegrationTest extends BaseIntegration {

  @Autowired
  private SavePost savePost;

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void save_ShouldSaveAndReturnPost() {
    // Given
    User testUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    SavePostDto savePostDto = PostFactory.createSavePostDto(testUser, TestDataSamples.NEW_POST_TITLE,
        TestDataSamples.NEW_POST_BODY);

    // When
    Post result = savePost.save(savePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getTitle()).isEqualTo(TestDataSamples.NEW_POST_TITLE);
    assertThat(result.getBody()).isEqualTo(TestDataSamples.NEW_POST_BODY);
    assertThat(result.getUser().getId()).isEqualTo(testUser.getId());
    assertThat(result.getTags()).isNotNull(); // Tags field should be present

    // Verify it was actually saved to the database
    Optional<Post> savedPost = postRepository.findById(result.getId());
    assertThat(savedPost).isPresent();
    assertThat(savedPost.get().getTitle()).isEqualTo(TestDataSamples.NEW_POST_TITLE);
    assertThat(savedPost.get().getTags()).isNotNull(); // Tags field should be present
  }
}
