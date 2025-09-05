package es.jmjg.experiments.application.post;

import es.jmjg.experiments.application.post.dto.UpdatePostTagsDto;
import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;
import es.jmjg.experiments.shared.PostFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePostTagsTest {
  @Mock
  private PostRepository postRepository;
  @Mock
  private ProcessPostTags processPostTags;
  @InjectMocks
  private UpdatePostTags updatePostTags;

  private UUID postUuid;
  private Post post;
  private AuthenticatedUserDto ownerUserDto;
  private AuthenticatedUserDto adminUserDto;
  private AuthenticatedUserDto otherUserDto;

  @BeforeEach
  void setUp() {
    User user = UserFactory.createBasicUser();
    post = PostFactory.createBasicPost(user);
    postUuid = post.getUuid();

    ownerUserDto = AuthenticatedUserFactory.createAuthenticatedUserDto(user);
    adminUserDto = AuthenticatedUserFactory.createAuthenticatedUserDto(UserFactory.createAdminUser());
    otherUserDto = AuthenticatedUserFactory.createAuthenticatedUserDto(UserFactory.createUser("otheruser", "other@email", "otherusername"));
  }

  @Test
  void update_ok_owner() {
    // given
    UpdatePostTagsDto dto = new UpdatePostTagsDto(postUuid, List.of("java", "spring"), ownerUserDto);
    when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
    doNothing().when(processPostTags).processTagsForPost(post, dto.tagNames());
    when(postRepository.save(post)).thenReturn(post);

    // when
    Post result = updatePostTags.update(dto);

    // then
    assertEquals(post, result);
    verify(processPostTags).processTagsForPost(post, dto.tagNames());
    verify(postRepository).save(post);
  }

  @Test
  void update_ok_admin() {
    // given
    UpdatePostTagsDto dto = new UpdatePostTagsDto(postUuid, List.of("java", "spring"), adminUserDto);
    when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
    doNothing().when(processPostTags).processTagsForPost(post, dto.tagNames());
    when(postRepository.save(post)).thenReturn(post);

    // when
    Post result = updatePostTags.update(dto);

    // then
    assertEquals(post, result);
    verify(processPostTags).processTagsForPost(post, dto.tagNames());
    verify(postRepository).save(post);
  }

  @Test
  void update_tagNotFound() {
    // give
    UpdatePostTagsDto dto = new UpdatePostTagsDto(postUuid, List.of("java", "notfound"), ownerUserDto);
    when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
    doThrow(new TagNotFound("Tag not found: notfound")).when(processPostTags).processTagsForPost(post, dto.tagNames());

    // when - then
    assertThrows(TagNotFound.class, () -> updatePostTags.update(dto));
    verify(processPostTags).processTagsForPost(post, dto.tagNames());
    verify(postRepository, never()).save(any());
  }

  @Test
  void update_postNotFound() {
    // given
    UpdatePostTagsDto dto = new UpdatePostTagsDto(postUuid, List.of("java", "spring"), ownerUserDto);
    when(postRepository.findByUuid(postUuid)).thenReturn(Optional.empty());

    // when - then
    assertThrows(PostNotFound.class, () -> updatePostTags.update(dto));
    verify(processPostTags, never()).processTagsForPost(any(), any());
    verify(postRepository, never()).save(any());
  }

  @Test
  void update_forbidden() {
    // given
    UpdatePostTagsDto dto = new UpdatePostTagsDto(postUuid, List.of("java", "spring"), otherUserDto);
    when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));

    // when - then
    assertThrows(Forbidden.class, () -> updatePostTags.update(dto));
    verify(processPostTags, never()).processTagsForPost(any(), any());
    verify(postRepository, never()).save(any());
  }
}
