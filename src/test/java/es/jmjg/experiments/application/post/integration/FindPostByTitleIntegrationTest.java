package es.jmjg.experiments.application.post.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.FindPostByTitle;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.shared.BaseIntegration;

class FindPostByTitleIntegrationTest extends BaseIntegration {

  @Autowired
  private FindPostByTitle findPostByTitle;

  // Sample posts from Flyway test migration data
  private static final String LEANNE_POST_TITLE = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";
  private static final String LEANNE_POST_BODY = "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto";

  private static final String ERVIN_POST_TITLE = "et ea vero quia laudantium autem";
  private static final String ERVIN_POST_BODY = "delectus reiciendis molestiae occaecati non minima eveniet qui voluptatibus\\naccusamus in eum beatae sit\\nvel qui neque voluptates ut commodi qui incidunt\\nut animi commodi";

  private static final String CLEMENTINE_POST_TITLE = "asperiores ea ipsam voluptatibus modi minima quia sint";
  private static final String CLEMENTINE_POST_BODY = "repellat aliquid praesentium dolorem quo\\nsed totam minus non itaque\\nnihil labore molestiae sunt dolor eveniet hic recusandae veniam\\ntempora et tenetur expedita sunt";

  @Test
  @Transactional
  void findByTitle_WhenTitleExists_ShouldReturnPost() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle(LEANNE_POST_TITLE);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(LEANNE_POST_TITLE);
    assertThat(result.get().getBody()).isEqualTo(LEANNE_POST_BODY);
    assertThat(result.get().getUser()).isNotNull();
    assertThat(result.get().getUser().getName()).isEqualTo("Leanne Graham");
  }

  @Test
  void findByTitle_WhenTitleDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle("Non-existent Post");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsNull_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle(null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsEmpty_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle("");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsWhitespace_ShouldReturnEmpty() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle("   ");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByTitle_WhenTitleIsUnique_ShouldReturnPost() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle(ERVIN_POST_TITLE);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(ERVIN_POST_TITLE);
    assertThat(result.get().getBody()).isEqualTo(ERVIN_POST_BODY);
    assertThat(result.get().getUser()).isNotNull();
    assertThat(result.get().getUser().getName()).isEqualTo("Ervin Howell");
  }

  @Test
  void findByTitle_WhenTitleExistsForDifferentUser_ShouldReturnPost() {
    // When
    Optional<Post> result = findPostByTitle.findByTitle(CLEMENTINE_POST_TITLE);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(CLEMENTINE_POST_TITLE);
    assertThat(result.get().getBody()).isEqualTo(CLEMENTINE_POST_BODY);
    assertThat(result.get().getUser()).isNotNull();
    assertThat(result.get().getUser().getName()).isEqualTo("Clementine Bauch");
  }
}
