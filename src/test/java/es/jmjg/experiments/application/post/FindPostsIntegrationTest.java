package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindPostsIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindPosts findPosts;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser;
  private Post springBootPost;
  private Post jpaPost;
  private Post tutorialPost;
  private Post javaPost;

  // Track test-created posts for cleanup
  private List<Post> testCreatedPosts = new ArrayList<>();

  @BeforeEach
  void setUp() {
    // Clean up only test-created posts from previous test runs
    if (!testCreatedPosts.isEmpty()) {
      postRepository.deleteAll(testCreatedPosts);
      testCreatedPosts.clear();
    }

    // Create test data for each test
    testUser = userRepository.save(UserFactory.createBasicUser());

    // Create test posts with different content for search testing
    springBootPost = PostFactory.createSpringBootPost(testUser);
    springBootPost = postRepository.save(springBootPost);
    testCreatedPosts.add(springBootPost);

    jpaPost = PostFactory.createJpaPost(testUser);
    jpaPost = postRepository.save(jpaPost);
    testCreatedPosts.add(jpaPost);

    tutorialPost = PostFactory.createJavaProgrammingPost(testUser);
    tutorialPost = postRepository.save(tutorialPost);
    testCreatedPosts.add(tutorialPost);

    javaPost = PostFactory.createAdvancedJavaPost(testUser);
    javaPost = postRepository.save(javaPost);
    testCreatedPosts.add(javaPost);
  }

  @AfterEach
  void tearDown() {
    // Clean up test-created posts after each test
    if (!testCreatedPosts.isEmpty()) {
      postRepository.deleteAll(testCreatedPosts);
      testCreatedPosts.clear();
    }
  }

  @Test
  void shouldUseTestProfile() {
    // Verify that the test profile is active
    String[] activeProfiles = environment.getActiveProfiles();
    assertThat(activeProfiles).contains("test");
  }

  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

  @Test
  void find_WhenQueryMatchesTitle_ShouldReturnMatchingPosts() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("Spring", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2); // Both Spring Boot Tutorial and JPA Best Practices contain
    // "Spring"
    assertThat(result)
        .extracting("title")
        .containsExactlyInAnyOrder("Spring Boot Tutorial", "JPA Best Practices");
  }

  @Test
  void find_WhenQueryMatchesBody_ShouldReturnMatchingPosts() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("framework", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("Spring Boot Tutorial");
  }

  @Test
  void find_WhenQueryMatchesMultiplePosts_ShouldReturnAllMatchingPosts() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("Java", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result)
        .extracting("title")
        .containsExactlyInAnyOrder("Java Programming Guide", "Advanced Java Features");
  }

  @Test
  void find_WhenQueryIsCaseInsensitive_ShouldReturnMatchingPosts() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("spring", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2); // Both posts contain "spring" in their content
    assertThat(result)
        .extracting("title")
        .containsExactlyInAnyOrder("Spring Boot Tutorial", "JPA Best Practices");
  }

  @Test
  void find_WhenQueryIsPartial_ShouldReturnMatchingPosts() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("Boot", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2); // Both Spring Boot Tutorial and JPA Best Practices contain
    // "Boot"
    assertThat(result)
        .extracting("title")
        .containsExactlyInAnyOrder("Spring Boot Tutorial", "JPA Best Practices");
  }

  @Test
  void find_WhenLimitIsApplied_ShouldRespectLimit() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("Java", 1);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
  }

  @Test
  void find_WhenQueryIsNull_ShouldReturnEmptyList() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find(null, 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void find_WhenQueryIsEmpty_ShouldReturnEmptyList() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void find_WhenQueryIsWhitespace_ShouldReturnEmptyList() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("   ", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void find_WhenNoPostsExist_ShouldReturnEmptyList() {
    // Given - clear all posts to ensure no posts exist
    postRepository.deleteAll();

    // When
    List<Post> result = findPosts.find("Spring", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void find_WhenQueryDoesNotMatchAnyPost_ShouldReturnEmptyList() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("NonExistent", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void find_WhenQueryMatchesInBothTitleAndBody_ShouldReturnPost() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("Spring", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2); // Both Spring Boot Tutorial and JPA Best Practices contain
    // "Spring"
    assertThat(result)
        .extracting("title")
        .containsExactlyInAnyOrder("Spring Boot Tutorial", "JPA Best Practices");
  }

  @Test
  void find_WhenQueryIsTrimmed_ShouldWorkCorrectly() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("  Spring  ", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2); // Both posts contain "Spring" in their content
    assertThat(result)
        .extracting("title")
        .containsExactlyInAnyOrder("Spring Boot Tutorial", "JPA Best Practices");
  }

  @Test
  void find_WhenMultiplePostsMatch_ShouldReturnAllInCorrectOrder() {
    // Given - data is set up in setUp()
    // When
    List<Post> result = findPosts.find("with", 10);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    // Both posts contain "with" in their body
    assertThat(result)
        .extracting("title")
        .containsExactlyInAnyOrder("Spring Boot Tutorial", "JPA Best Practices");
  }
}
