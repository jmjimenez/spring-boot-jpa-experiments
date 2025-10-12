package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.post.dto.DeletePostDto;
import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.post.repository.PostRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class DeletePostTest {

  @Mock
  private PostRepository postRepository;

  @InjectMocks
  private DeletePost deletePost;

  private User postOwner;
  private Post post;

  @BeforeEach
  void setUp() {
    postOwner = UserFactory.createBasicUser();
    post = PostFactory.createBasicPost(postOwner);
  }

  @Test
  void deleteByUuid_WhenPostExistsAndUserIsOwner_ShouldDeletePost() {
    when(postRepository.findByPostUuid(post.getPostUuid())).thenReturn(Optional.of(post));
    doNothing().when(postRepository).deleteById(post.getId());

    // When
    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(post.getPostUuid(), postOwner);
    deletePost.delete(deletePostDto);

    // Then
    verify(postRepository, times(1)).findByPostUuid(post.getPostUuid());
    verify(postRepository, times(1)).deleteById(post.getId());
  }

  @Test
  void deleteByUuid_WhenPostExistsAndUserIsAdmin_ShouldDeletePost() {
    when(postRepository.findByPostUuid(post.getPostUuid())).thenReturn(Optional.of(post));
    doNothing().when(postRepository).deleteById(post.getId());

    // When
    User adminUser = UserFactory.createAdminUser();
    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(post.getPostUuid(), adminUser);
    deletePost.delete(deletePostDto);

    // Then
    verify(postRepository, times(1)).findByPostUuid(post.getPostUuid());
    verify(postRepository, times(1)).deleteById(post.getId());
  }

  @Test
  void deleteByUuid_WhenPostDoesNotExist_ShouldThrowPostNotFound() {
    when(postRepository.findByPostUuid(post.getPostUuid())).thenReturn(Optional.empty());

    // When & Then
    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(post.getPostUuid(), postOwner);
    assertThatThrownBy(() -> deletePost.delete(deletePostDto))
        .isInstanceOf(PostNotFound.class)
        .hasMessage("Post not found with id: " + post.getPostUuid());

    verify(postRepository, times(1)).findByPostUuid(post.getPostUuid());
    verify(postRepository, never()).deleteById(any());
  }

  @Test
  void deleteByUuid_WhenUserIsNotOwnerAndNotAdmin_ShouldThrowForbidden() {
    when(postRepository.findByPostUuid(post.getPostUuid())).thenReturn(Optional.of(post));

    // When & Then
    User nonOwnerUser = UserFactory.createBasicUser();
    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(post.getPostUuid(), nonOwnerUser);
    assertThatThrownBy(() -> deletePost.delete(deletePostDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("You are not the owner of this post");

    verify(postRepository, times(1)).findByPostUuid(post.getPostUuid());
    verify(postRepository, never()).deleteById(any());
  }

  @Test
  void deleteByUuid_WhenUserHasBothUserAndAdminRoles_ShouldDeletePost() {
    when(postRepository.findByPostUuid(post.getPostUuid())).thenReturn(Optional.of(post));
    doNothing().when(postRepository).deleteById(post.getId());

    // When
    User adminUser = UserFactory.createAdminUser();
    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(post.getPostUuid(), adminUser);
    deletePost.delete(deletePostDto);

    // Then
    verify(postRepository, times(1)).findByPostUuid(post.getPostUuid());
    verify(postRepository, times(1)).deleteById(post.getId());
  }
}
