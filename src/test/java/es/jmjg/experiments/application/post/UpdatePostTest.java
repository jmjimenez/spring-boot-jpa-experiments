package es.jmjg.experiments.application.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.post.repository.PostRepository;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class UpdatePostTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private ProcessPostTags processPostTags;

  @InjectMocks
  private UpdatePost updatePost;

  private User testUser;
  private User testUser2;
  private User testUserAdmin;
  private Post testPost;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    testUser2 = UserFactory.createBasicUser();
    testUserAdmin = UserFactory.createAdminUser();
    testPost = PostFactory.createBasicPost(testUser);
  }

  @Test
  void update_WhenPostExistsAndUserIsOwner_ShouldUpdatePost() {
    // Given
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> invocation.<Post>getArgument(0));
    doNothing().when(processPostTags).processTagsForPost(any(Post.class), anyList());

    // When
    var updatePostDto = PostFactory.createPostUpdateDto(testPost.getUuid(), "Updated Title", "Updated Body", testUser);
    Post result = updatePost.update(updatePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo(updatePostDto.title());
    assertThat(result.getBody()).isEqualTo(updatePostDto.body());
    assertThat(result.getTags()).isNotNull(); // Tags field should be present

    verify(postRepository, times(1)).findByUuid(testPost.getUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenPostExistsAndUserIsAdmin_ShouldUpdatePost() {
    // Given
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> invocation.<Post>getArgument(0));
    doNothing().when(processPostTags).processTagsForPost(any(Post.class), anyList());

    // When
    var updatePostDto = PostFactory.createPostUpdateDto(testPost.getUuid(), "Updated Title", "Updated Body", testUserAdmin);
    Post result = updatePost.update(updatePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo(updatePostDto.title());
    assertThat(result.getBody()).isEqualTo(updatePostDto.body());
    assertThat(result.getTags()).isNotNull(); // Tags field should be present

    verify(postRepository, times(1)).findByUuid(testPost.getUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenPostExistsAndUserIsNotOwner_ShouldThrowForbidden() {
    // Given
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.of(testPost));

    // When
    var updatePostDto = PostFactory.createPostUpdateDto(testPost.getUuid(), "Updated Title", "Updated Body", testUser2);
    assertThatThrownBy(() -> updatePost.update(updatePostDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("You are not the owner of this post");

    // Then
    verify(postRepository, times(1)).findByUuid(testPost.getUuid());
    verify(postRepository, never()).save(any());
  }

  @Test
  void update_WhenPostDoesNotExist_ShouldThrowPostNotFound() {
    // Given
    UUID nonExistentPostUuid = UUID.randomUUID();
    when(postRepository.findByUuid(nonExistentPostUuid)).thenReturn(Optional.empty());

    // When & Then
    var updatePostDto = PostFactory.createPostUpdateDto(nonExistentPostUuid, "Updated Title", "Updated Body", testUser);
    assertThatThrownBy(() -> updatePost.update(updatePostDto))
        .isInstanceOf(PostNotFound.class)
        .hasMessage("Post not found with uuid: " + nonExistentPostUuid);

    verify(postRepository, times(1)).findByUuid(nonExistentPostUuid);
    verify(postRepository, never()).save(any());
  }

  @Test
  void update_ShouldPreserveOriginalUuidAndUserId() {
    // Given
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> invocation.<Post>getArgument(0));
    doNothing().when(processPostTags).processTagsForPost(any(Post.class), anyList());

    // When
    var updatePostDto = PostFactory.createPostUpdateDto(testPost.getUuid(), "Updated Title", "Updated Body", testUser);
    Post result = updatePost.update(updatePostDto);

    // Then
    assertThat(result.getUuid()).isEqualTo(testPost.getUuid());
    assertThat(result.getUser().getUuid()).isEqualTo(testUser.getUuid());
    assertThat(result.getTitle()).isEqualTo(updatePostDto.title());
    assertThat(result.getBody()).isEqualTo(updatePostDto.body());
    assertThat(result.getTags()).isNotNull();

    verify(postRepository, times(1)).findByUuid(testPost.getUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenPostHasUser_ShouldNotUsePostUser() {
    // Given
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> invocation.<Post>getArgument(0));
    doNothing().when(processPostTags).processTagsForPost(any(Post.class), anyList());

    // When
    var updatePostDto = PostFactory.createPostUpdateDto(testPost.getUuid(), "Updated Title", "Updated Body", testUser);
    Post result = updatePost.update(updatePostDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUser()).isEqualTo(testUser);
    assertThat(result.getTitle()).isEqualTo(updatePostDto.title());
    assertThat(result.getBody()).isEqualTo(updatePostDto.body());
    assertThat(result.getTags()).isNotNull(); // Tags field should be present

    verify(postRepository, times(1)).findByUuid(testPost.getUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void update_WhenUuidProvided_ShouldNotUpdateUuid() {
    // Given
    when(postRepository.findByUuid(testPost.getUuid())).thenReturn(Optional.of(testPost));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(
            invocation -> invocation.<Post>getArgument(0));
    doNothing().when(processPostTags).processTagsForPost(any(Post.class), anyList());

    // When
    var updatePostDto = PostFactory.createPostUpdateDto(testPost.getUuid(), "Updated Title", "Updated Body", testUser);
    Post result = updatePost.update(updatePostDto);

    // Then
    assertThat(result.getUuid()).isEqualTo(testPost.getUuid());
    assertThat(result.getTags()).isNotNull(); // Tags field should be present
    verify(postRepository, times(1)).findByUuid(testPost.getUuid());
    verify(postRepository, times(1)).save(any(Post.class));
  }
}
