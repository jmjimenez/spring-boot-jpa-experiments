package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.post.DeletePostComment;
import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostControllerDeleteCommentTest extends BasePostControllerTest {
  @Autowired
  private DeletePostComment deletePostComment;

  private static final UUID NON_EXISTENT_POST_COMMENT_UUID = UUID.randomUUID();

  @Test
  void shouldDeletePostCommentWhenGivenValidUUIDAndValidOwner() throws Exception {
    mockMvc.perform(delete("/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID)
        .header("Authorization", "Bearer " + TestDataSamples.ADMIN_USERNAME))
        .andExpect(status().isNoContent());

    verify(deletePostComment, times(1))
        .delete(argThat(dto -> dto.uuid().equals(TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID) &&
            dto.authenticatedUser().id().equals(TestDataSamples.ADMIN_UUID) &&
            dto.authenticatedUser().username().equals(TestDataSamples.ADMIN_USERNAME)));
  }

  @Test
  void shouldReturnNotFoundWhenPostUuidDoesNotExist() throws Exception {
    doThrow(new PostNotFound(NON_EXISTENT_POST_COMMENT_UUID)).when(deletePostComment)
        .delete(argThat(dto -> dto.uuid().equals(NON_EXISTENT_POST_COMMENT_UUID)));

    mockMvc.perform(delete("/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + NON_EXISTENT_POST_COMMENT_UUID)
        .header("Authorization", "Bearer " + TestDataSamples.ADMIN_USERNAME))
        .andExpect(status().isNotFound());

    verify(deletePostComment, times(1)).delete(argThat(dto -> dto.uuid().equals(
      NON_EXISTENT_POST_COMMENT_UUID) &&
        dto.authenticatedUser().username().equals(TestDataSamples.ADMIN_USERNAME)));
  }

  @Test
  void shouldReturnForbiddenWhenAuthenticatedUserIsNeitherOwnerNorAdmin() throws Exception {
    doThrow(new Forbidden("You are not the owner of this post")).when(deletePostComment)
        .delete(argThat(dto -> dto.uuid().equals(TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID) &&
            dto.authenticatedUser().username().equals(TestDataSamples.LEANNE_USERNAME)));

    mockMvc.perform(delete("/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID)
        .header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
        .andExpect(status().isForbidden());

    verify(deletePostComment, times(1))
        .delete(argThat(dto -> dto.uuid().equals(TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID) &&
            dto.authenticatedUser().username().equals(TestDataSamples.LEANNE_USERNAME)));
  }

  @Test
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
    mockMvc.perform(delete("/api/posts/" + TestDataSamples.LEANNE_POST_UUID + "/comments/" + TestDataSamples.COMMENT_LEANNE_POST_BY_ERWIN_UUID))
        .andExpect(status().isUnauthorized());

    verify(deletePostComment, never()).delete(any());
  }
}
