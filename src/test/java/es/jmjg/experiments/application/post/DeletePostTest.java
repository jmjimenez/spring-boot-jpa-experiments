package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.post.dto.DeletePostDto;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class DeletePostTest {

  private static final Integer POST_ID = 1;
  private static final UUID POST_UUID = UUID.randomUUID();

  private static final UUID USER_UUID = UUID.randomUUID();
  private static final UUID DIFFERENT_USER_UUID = UUID.randomUUID();

  private static final String USER_USERNAME = "testuser";
  private static final String DIFFERENT_USERNAME = "differentuser";
  private static final String ADMIN_USERNAME = "admin";

  @Mock
  private PostRepository postRepository;

  @InjectMocks
  private DeletePost deletePost;

  @Test
  void deleteByUuid_WhenPostExistsAndUserIsOwner_ShouldDeletePost() {
    // Given
    User postOwner = UserFactory.createBasicUser();
    postOwner.setUuid(USER_UUID);

    Post post = new Post();
    post.setId(POST_ID);
    post.setUuid(POST_UUID);
    post.setUser(postOwner);

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(POST_UUID, postOwner);

    when(postRepository.findByUuid(POST_UUID)).thenReturn(Optional.of(post));
    doNothing().when(postRepository).deleteById(POST_ID);

    // When
    deletePost.delete(deletePostDto);

    // Then
    verify(postRepository, times(1)).findByUuid(POST_UUID);
    verify(postRepository, times(1)).deleteById(POST_ID);
  }

  @Test
  void deleteByUuid_WhenPostExistsAndUserIsAdmin_ShouldDeletePost() {
    // Given
    User postOwner = UserFactory.createBasicUser();
    postOwner.setUuid(USER_UUID);

    Post post = new Post();
    post.setId(POST_ID);
    post.setUuid(POST_UUID);
    post.setUser(postOwner);

    User adminUser = UserFactory.createBasicUser();
    adminUser.setUuid(DIFFERENT_USER_UUID);
    adminUser.setUsername(ADMIN_USERNAME);

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(POST_UUID, adminUser);

    when(postRepository.findByUuid(POST_UUID)).thenReturn(Optional.of(post));
    doNothing().when(postRepository).deleteById(POST_ID);

    // When
    deletePost.delete(deletePostDto);

    // Then
    verify(postRepository, times(1)).findByUuid(POST_UUID);
    verify(postRepository, times(1)).deleteById(POST_ID);
  }

  @Test
  void deleteByUuid_WhenPostDoesNotExist_ShouldThrowPostNotFound() {
    // Given
    User testUser = UserFactory.createBasicUser();
    testUser.setUuid(USER_UUID);
    testUser.setUsername(USER_USERNAME);

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(POST_UUID, testUser);

    when(postRepository.findByUuid(POST_UUID)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> deletePost.delete(deletePostDto))
        .isInstanceOf(PostNotFound.class)
        .hasMessage("Post not found with uuid: " + POST_UUID);

    verify(postRepository, times(1)).findByUuid(POST_UUID);
    verify(postRepository, never()).deleteById(any());
  }

  @Test
  void deleteByUuid_WhenUserIsNotOwnerAndNotAdmin_ShouldThrowForbidden() {
    // Given
    User postOwner = UserFactory.createBasicUser();
    postOwner.setUuid(USER_UUID);

    Post post = new Post();
    post.setId(POST_ID);
    post.setUuid(POST_UUID);
    post.setUser(postOwner);

    User nonOwnerUser = UserFactory.createBasicUser();
    nonOwnerUser.setUuid(DIFFERENT_USER_UUID);
    nonOwnerUser.setUsername(DIFFERENT_USERNAME);

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(POST_UUID, nonOwnerUser);

    when(postRepository.findByUuid(POST_UUID)).thenReturn(Optional.of(post));

    // When & Then
    assertThatThrownBy(() -> deletePost.delete(deletePostDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("You are not the owner of this post");

    verify(postRepository, times(1)).findByUuid(POST_UUID);
    verify(postRepository, never()).deleteById(any());
  }

  @Test
  void deleteByUuid_WhenUserHasBothUserAndAdminRoles_ShouldDeletePost() {
    // Given
    User postOwner = UserFactory.createBasicUser();
    postOwner.setUuid(USER_UUID);

    Post post = new Post();
    post.setId(POST_ID);
    post.setUuid(POST_UUID);
    post.setUser(postOwner);

    User adminUser = UserFactory.createBasicUser();
    adminUser.setUuid(DIFFERENT_USER_UUID);
    adminUser.setUsername(ADMIN_USERNAME);

    DeletePostDto deletePostDto = PostFactory.createDeletePostDto(POST_UUID, adminUser);

    when(postRepository.findByUuid(POST_UUID)).thenReturn(Optional.of(post));
    doNothing().when(postRepository).deleteById(POST_ID);

    // When
    deletePost.delete(deletePostDto);

    // Then
    verify(postRepository, times(1)).findByUuid(POST_UUID);
    verify(postRepository, times(1)).deleteById(POST_ID);
  }
}
