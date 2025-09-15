package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.jmjg.experiments.application.post.dto.DeletePostCommentDto;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.exception.PostCommentNotFound;
import es.jmjg.experiments.domain.post.repository.PostCommentRepository;
import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeletePostCommentTest {

  @Mock
  private PostCommentRepository postCommentRepository;

  @InjectMocks
  private DeletePostComment deletePostComment;

  private PostComment postComment;

  @BeforeEach
  void setUp() {
    User postOwner = UserFactory.createBasicUser();
    Post post = PostFactory.createBasicPost(postOwner);
    postComment = PostFactory.createPostComment(postOwner, post,  "Sample comment");
  }

  @Test
  void deleteByUuid_WhenPostCommentExistsAndUserIsAdmin_ShouldDeletePost() {
    when(postCommentRepository.findByUuid(postComment.getUuid())).thenReturn(Optional.of(postComment));
    doNothing().when(postCommentRepository).deleteById(postComment.getId());

    // When
    User adminUser = UserFactory.createAdminUser();
    DeletePostCommentDto dto = PostFactory.createDeletePostCommentDto(postComment.getPost().getUuid(), postComment.getUuid(), adminUser);
    deletePostComment.delete(dto);

    // Then
    verify(postCommentRepository, times(1)).findByUuid(postComment.getUuid());
    verify(postCommentRepository, times(1)).deleteById(postComment.getId());
  }

  @Test
  void deleteByUuid_WhenPostCommentDoesNotExist_ShouldThrowPostCommentNotFound() {
    when(postCommentRepository.findByUuid(postComment.getUuid())).thenReturn(Optional.empty());

    // When & Then
    User adminUser = UserFactory.createAdminUser();
    DeletePostCommentDto dto = PostFactory.createDeletePostCommentDto(postComment.getPost().getUuid(), postComment.getUuid(), adminUser);
    assertThatThrownBy(() -> deletePostComment.delete(dto))
        .isInstanceOf(PostCommentNotFound.class)
        .hasMessage("Post comment with id " + postComment.getUuid() + " not found");

    verify(postCommentRepository, times(1)).findByUuid(postComment.getUuid());
    verify(postCommentRepository, never()).deleteById(any());
  }

  @Test
  void deleteByUuid_WhenUserIsNotAdmin_ShouldThrowForbidden() {
    // When & Then
    User nonOwnerUser = UserFactory.createBasicUser();
    DeletePostCommentDto dto = PostFactory.createDeletePostCommentDto(postComment.getPost().getUuid(), postComment.getUuid(), nonOwnerUser);
    assertThatThrownBy(() -> deletePostComment.delete(dto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("You are not allowed to delete comments");

    verify(postCommentRepository, never()).findByUuid(postComment.getUuid());
    verify(postCommentRepository, never()).deleteById(any());
  }
}
