package es.jmjg.experiments.application.post.shared;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.tag.repository.TagRepository;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessPostTagsTest {

  @Mock
  private TagRepository tagRepository;

  @InjectMocks
  private ProcessPostTags processPostTags;

  @Test
  void WhenTagsAreCorrect_ShouldAssigneTagsTothePost() {
    Post post = mock(Post.class);
    List<String> tags = Arrays.asList("tag1", "tag2");

    when(tagRepository.findByName("tag1")).thenReturn(of(new Tag(1, UUID.randomUUID(), "tag1")));
    when(tagRepository.findByName("tag2")).thenReturn(of(new Tag(2, UUID.randomUUID(), "tag2")));

    processPostTags.processTagsForPost(post, tags);

    // Then
    verify(post, times(1)).setTags(argThat(list -> list.size() == 2
      && list.stream().anyMatch(tag -> tag.getName().equals("tag1"))
      && list.stream().anyMatch(tag -> tag.getName().equals("tag2"))));
  }

  @Test
  void WhenTagIsNotFound_ShouldThrowTagNotFound() {
    Post post = mock(Post.class);
    List<String> tags = Arrays.asList("tag1", "tag2");

    when(tagRepository.findByName("tag1")).thenReturn(of(new Tag(1, UUID.randomUUID(), "tag1")));
    when(tagRepository.findByName("tag2")).thenReturn(empty());

    assertThatThrownBy(() -> processPostTags.processTagsForPost(post, tags))
      .isInstanceOf(TagNotFound.class)
      .hasMessage("Tag not found: " + "tag2");

    // Then
    verify(post, times(0)).setTags(any());
  }
}
