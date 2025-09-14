package es.jmjg.experiments.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.BaseJpaIntegration;
import es.jmjg.experiments.shared.TestDataSamples;

@Import({ PostRepositoryImpl.class, TagRepositoryImpl.class, UserRepositoryImpl.class })
public class PostRepositoryIntegrationTest extends BaseJpaIntegration {

  @Autowired
  private PostRepositoryImpl postRepository;

  @Autowired
  private TagRepositoryImpl tagRepository;

  @Autowired
  private UserRepositoryImpl userRepository;

  private User leanneUser;
  private Tag technologyTag;
  private Tag javaTag;
  private Tag springBootTag;

  @BeforeEach
  void setUp() {
    // Create test user and tags for tag assignment tests
    leanneUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    technologyTag = tagRepository.findByUuid(TestDataSamples.TECHNOLOGY_UUID).orElseThrow();
    javaTag = tagRepository.findByUuid(TestDataSamples.JAVA_UUID).orElseThrow();
    springBootTag = tagRepository.findByUuid(TestDataSamples.SPRING_BOOT_UUID).orElseThrow();
  }

  @Test
  void shouldReturnPostByTitle() {
    Post post = postRepository.findByTitle(TestDataSamples.LEANNE_POST_TITLE).orElseThrow();
    assertEquals(TestDataSamples.LEANNE_POST_TITLE, post.getTitle(), "Post title should match the migration data");
    assertEquals(TestDataSamples.LEANNE_POST_UUID, post.getUuid(), "Post UUID should match the migration data");
  }

  @Test
  void shouldNotReturnPostWhenTitleIsNotFound() {
    Optional<Post> post = postRepository.findByTitle("Hello, Wrong Title!");
    assertFalse(post.isPresent(), "Post should not be present");
  }

  @Test
  void shouldFindMultiplePostsByTitle() {
    // Test finding posts from different users
    Optional<Post> leannePost = postRepository.findByTitle(TestDataSamples.LEANNE_POST_TITLE);
    Optional<Post> ervinPost = postRepository.findByTitle(TestDataSamples.ERVIN_POST_TITLE);
    Optional<Post> clementinePost = postRepository.findByTitle(TestDataSamples.CLEMENTINE_POST_TITLE);

    assertTrue(leannePost.isPresent(), "Leanne's post should be found");
    assertTrue(ervinPost.isPresent(), "Ervin's post should be found");
    assertTrue(clementinePost.isPresent(), "Clementine's post should be found");

    assertEquals(TestDataSamples.LEANNE_POST_UUID, leannePost.get().getUuid());
    assertEquals(TestDataSamples.ERVIN_POST_UUID, ervinPost.get().getUuid());
    assertEquals(TestDataSamples.CLEMENTINE_POST_UUID, clementinePost.get().getUuid());
  }

  @Test
  void findAll_WithPagination_ShouldReturnCorrectPage() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<Post> result = postRepository.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(10); // First page with 10 items
    assertThat(result.getTotalElements()).isEqualTo(50); // Exactly 50 posts from migration
    assertThat(result.getTotalPages()).isEqualTo(5); // 5 pages with 10 items per page
    assertThat(result.getNumber()).isEqualTo(0);
  }

  @Test
  void findAll_WithPagination_ShouldReturnEmptyPageWhenNoPosts() {
    // Given - Test with a page that should be empty
    Pageable pageable = PageRequest.of(999, 10); // Very high page number

    // When
    Page<Post> result = postRepository.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isEqualTo(50); // Exactly 50 posts from migration
    assertThat(result.getNumber()).isEqualTo(999);
  }

  @Test
  void findAll_WithPagination_ShouldHandleMultiplePages() {
    // Given
    Pageable firstPage = PageRequest.of(0, 5);
    Pageable secondPage = PageRequest.of(1, 5);

    // When
    Page<Post> firstPageResult = postRepository.findAll(firstPage);
    Page<Post> secondPageResult = postRepository.findAll(secondPage);

    // Then
    assertThat(firstPageResult.getContent()).hasSize(5);
    assertThat(firstPageResult.getTotalElements()).isEqualTo(50);
    assertThat(firstPageResult.getTotalPages()).isEqualTo(10); // 10 pages with 5 items per page
    assertThat(firstPageResult.getNumber()).isEqualTo(0);

    assertThat(secondPageResult.getContent()).hasSize(5);
    assertThat(secondPageResult.getTotalElements()).isEqualTo(50);
    assertThat(secondPageResult.getTotalPages()).isEqualTo(10);
    assertThat(secondPageResult.getNumber()).isEqualTo(1);

    // Verify that the pages contain different content
    assertThat(firstPageResult.getContent()).isNotEqualTo(secondPageResult.getContent());
  }

  @Test
  void shouldFindAllPostsWithCorrectTotalCount() {
    // Given
    Pageable allPostsPage = PageRequest.of(0, 50);

    // When
    Page<Post> result = postRepository.findAll(allPostsPage);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(50); // All 50 posts from migration
    assertThat(result.getTotalElements()).isEqualTo(50);
    assertThat(result.getTotalPages()).isEqualTo(1); // Single page with all posts
    assertThat(result.getNumber()).isEqualTo(0);
  }

  @Test
  void save_WhenPostHasTags_ShouldSavePostWithTags() {
    // Given
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(leanneUser);
    post.setTitle("Java Programming");
    post.setBody("Learn Java programming");

    List<Tag> tags = new ArrayList<>();
    tags.add(technologyTag);
    tags.add(javaTag);
    post.setTags(tags);

    // When
    Post savedPost = postRepository.save(post);

    // Then
    assertThat(savedPost).isNotNull();
    assertThat(savedPost.getId()).isNotNull();
    assertThat(savedPost.getTitle()).isEqualTo("Java Programming");
    assertThat(savedPost.getBody()).isEqualTo("Learn Java programming");
    assertThat(savedPost.getUser().getId()).isEqualTo(leanneUser.getId());
    assertThat(savedPost.getTags()).hasSize(2);
    assertThat(savedPost.getTags()).extracting("name").containsExactlyInAnyOrder(TestDataSamples.TECHNOLOGY_TAG_NAME, TestDataSamples.TAG_JAVA);

    // Verify it was actually saved to the database with tags
    Optional<Post> retrievedPost = postRepository.findById(savedPost.getId());
    assertThat(retrievedPost).isPresent();
    assertThat(retrievedPost.get().getTags()).hasSize(2);
    assertThat(retrievedPost.get().getTags()).extracting("name").containsExactlyInAnyOrder(TestDataSamples.TECHNOLOGY_TAG_NAME,
      TestDataSamples.TAG_JAVA);
  }

  @Test
  void save_WhenPostHasSingleTag_ShouldSavePostWithTag() {
    // Given
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(leanneUser);
    post.setTitle("Spring Boot Tutorial");
    post.setBody("Learn Spring Boot");

    List<Tag> tags = new ArrayList<>();
    tags.add(springBootTag);
    post.setTags(tags);

    // When
    Post savedPost = postRepository.save(post);

    // Then
    assertThat(savedPost).isNotNull();
    assertThat(savedPost.getId()).isNotNull();
    assertThat(savedPost.getTitle()).isEqualTo("Spring Boot Tutorial");
    assertThat(savedPost.getBody()).isEqualTo("Learn Spring Boot");
    assertThat(savedPost.getUser().getId()).isEqualTo(leanneUser.getId());
    assertThat(savedPost.getTags()).hasSize(1);
    assertThat(savedPost.getTags().getFirst().getName()).isEqualTo(TestDataSamples.SPRING_BOOT_TAG_NAME);

    // Verify it was actually saved to the database with tag
    Optional<Post> retrievedPost = postRepository.findById(savedPost.getId());
    assertThat(retrievedPost).isPresent();
    assertThat(retrievedPost.get().getTags()).hasSize(1);
    assertThat(retrievedPost.get().getTags().getFirst().getName()).isEqualTo(TestDataSamples.SPRING_BOOT_TAG_NAME);
  }

  @Test
  void save_WhenPostHasNoTags_ShouldSavePostWithoutTags() {
    // Given
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(leanneUser);
    post.setTitle("Post Without Tags");
    post.setBody("This post has no tags");
    post.setTags(new ArrayList<>());

    // When
    Post savedPost = postRepository.save(post);

    // Then
    assertThat(savedPost).isNotNull();
    assertThat(savedPost.getId()).isNotNull();
    assertThat(savedPost.getTitle()).isEqualTo("Post Without Tags");
    assertThat(savedPost.getBody()).isEqualTo("This post has no tags");
    assertThat(savedPost.getUser().getId()).isEqualTo(leanneUser.getId());
    assertThat(savedPost.getTags()).isEmpty();

    // Verify it was actually saved to the database without tags
    Optional<Post> retrievedPost = postRepository.findById(savedPost.getId());
    assertThat(retrievedPost).isPresent();
    assertThat(retrievedPost.get().getTags()).isEmpty();
  }

  @Test
  void save_WhenPostHasMultipleTags_ShouldSavePostWithAllTags() {
    // Given
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(leanneUser);
    post.setTitle("Full Stack Java");
    post.setBody("Complete Java development guide");

    List<Tag> tags = new ArrayList<>();
    tags.add(technologyTag);
    tags.add(javaTag);
    tags.add(springBootTag);
    post.setTags(tags);

    // When
    Post savedPost = postRepository.save(post);

    // Then
    assertThat(savedPost).isNotNull();
    assertThat(savedPost.getId()).isNotNull();
    assertThat(savedPost.getTitle()).isEqualTo("Full Stack Java");
    assertThat(savedPost.getBody()).isEqualTo("Complete Java development guide");
    assertThat(savedPost.getUser().getId()).isEqualTo(leanneUser.getId());
    assertThat(savedPost.getTags()).hasSize(3);
    assertThat(savedPost.getTags()).extracting("name").containsExactlyInAnyOrder(
        TestDataSamples.TECHNOLOGY_TAG_NAME, TestDataSamples.TAG_JAVA, TestDataSamples.SPRING_BOOT_TAG_NAME);

    // Verify it was actually saved to the database with all tags
    Optional<Post> retrievedPost = postRepository.findById(savedPost.getId());
    assertThat(retrievedPost).isPresent();
    assertThat(retrievedPost.get().getTags()).hasSize(3);
    assertThat(retrievedPost.get().getTags()).extracting("name").containsExactlyInAnyOrder(
      TestDataSamples.TECHNOLOGY_TAG_NAME, TestDataSamples.TAG_JAVA, TestDataSamples.SPRING_BOOT_TAG_NAME);
  }

  @Test
  void update_WhenPostHasTags_ShouldUpdatePostWithTags() {
    // Given - First save a post without tags
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(leanneUser);
    post.setTitle("Original Post");
    post.setBody("Original content");
    post.setTags(new ArrayList<>());

    Post savedPost = postRepository.save(post);

    // When - Update the post with tags
    savedPost.setTitle("Updated Post");
    savedPost.setBody("Updated content");
    List<Tag> tags = new ArrayList<>();
    tags.add(technologyTag);
    tags.add(javaTag);
    savedPost.setTags(tags);

    Post updatedPost = postRepository.save(savedPost);

    // Then
    assertThat(updatedPost).isNotNull();
    assertThat(updatedPost.getId()).isEqualTo(savedPost.getId());
    assertThat(updatedPost.getTitle()).isEqualTo("Updated Post");
    assertThat(updatedPost.getBody()).isEqualTo("Updated content");
    assertThat(updatedPost.getUser().getId()).isEqualTo(leanneUser.getId());
    assertThat(updatedPost.getTags()).hasSize(2);
    assertThat(updatedPost.getTags()).extracting("name").containsExactlyInAnyOrder(TestDataSamples.TECHNOLOGY_TAG_NAME, TestDataSamples.TAG_JAVA);

    // Verify it was actually updated in the database with tags
    Optional<Post> retrievedPost = postRepository.findById(updatedPost.getId());
    assertThat(retrievedPost).isPresent();
    assertThat(retrievedPost.get().getTags()).hasSize(2);
    assertThat(retrievedPost.get().getTags()).extracting("name").containsExactlyInAnyOrder(TestDataSamples.TECHNOLOGY_TAG_NAME,
      TestDataSamples.TAG_JAVA);
  }

  @Test
  void update_WhenPostTagsAreRemoved_ShouldUpdatePostWithoutTags() {
    // Given - First save a post with tags
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(leanneUser);
    post.setTitle("Post With Tags");
    post.setBody("Content with tags");

    List<Tag> tags = new ArrayList<>();
    tags.add(technologyTag);
    tags.add(javaTag);
    post.setTags(tags);

    Post savedPost = postRepository.save(post);

    // When - Update the post to remove tags
    savedPost.setTitle("Post Without Tags");
    savedPost.setBody("Content without tags");
    savedPost.setTags(new ArrayList<>());

    Post updatedPost = postRepository.save(savedPost);

    // Then
    assertThat(updatedPost).isNotNull();
    assertThat(updatedPost.getId()).isEqualTo(savedPost.getId());
    assertThat(updatedPost.getTitle()).isEqualTo("Post Without Tags");
    assertThat(updatedPost.getBody()).isEqualTo("Content without tags");
    assertThat(updatedPost.getUser().getId()).isEqualTo(leanneUser.getId());
    assertThat(updatedPost.getTags()).isEmpty();

    // Verify it was actually updated in the database without tags
    Optional<Post> retrievedPost = postRepository.findById(updatedPost.getId());
    assertThat(retrievedPost).isPresent();
    assertThat(retrievedPost.get().getTags()).isEmpty();
  }

  @Test
  void update_WhenPostTagsAreModified_ShouldUpdatePostWithNewTags() {
    // Given - First save a post with initial tags
    Post post = new Post();
    post.setUuid(UUID.randomUUID());
    post.setUser(leanneUser);
    post.setTitle("Post With Initial Tags");
    post.setBody("Content with initial tags");

    List<Tag> initialTags = new ArrayList<>();
    initialTags.add(technologyTag);
    post.setTags(initialTags);

    Post savedPost = postRepository.save(post);

    // When - Update the post with different tags
    savedPost.setTitle("Post With Modified Tags");
    savedPost.setBody("Content with modified tags");
    List<Tag> newTags = new ArrayList<>();
    newTags.add(javaTag);
    newTags.add(springBootTag);
    savedPost.setTags(newTags);

    Post updatedPost = postRepository.save(savedPost);

    // Then
    assertThat(updatedPost).isNotNull();
    assertThat(updatedPost.getId()).isEqualTo(savedPost.getId());
    assertThat(updatedPost.getTitle()).isEqualTo("Post With Modified Tags");
    assertThat(updatedPost.getBody()).isEqualTo("Content with modified tags");
    assertThat(updatedPost.getUser().getId()).isEqualTo(leanneUser.getId());
    assertThat(updatedPost.getTags()).hasSize(2);
    assertThat(updatedPost.getTags()).extracting("name").containsExactlyInAnyOrder(TestDataSamples.TAG_JAVA, TestDataSamples.SPRING_BOOT_TAG_NAME);

    // Verify it was actually updated in the database with new tags
    Optional<Post> retrievedPost = postRepository.findById(updatedPost.getId());
    assertThat(retrievedPost).isPresent();
    assertThat(retrievedPost.get().getTags()).hasSize(2);
    assertThat(retrievedPost.get().getTags()).extracting("name").containsExactlyInAnyOrder(TestDataSamples.TAG_JAVA,
      TestDataSamples.SPRING_BOOT_TAG_NAME);
  }
}
