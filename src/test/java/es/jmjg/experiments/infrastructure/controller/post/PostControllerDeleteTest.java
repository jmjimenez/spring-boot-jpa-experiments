package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class PostControllerDeleteTest extends BasePostControllerTest {

  private static final UUID NON_EXISTENT_POST_UUID = UUID.randomUUID();

  @Test
  void shouldDeletePostWhenGivenValidUUIDAndValidOwner() throws Exception {
    mockMvc.perform(delete("/api/posts/" + TestDataSamples.POST_2_UUID)
        .header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
        .andExpect(status().isNoContent());

    verify(deletePost, times(1))
        .delete(argThat(deletePostDto -> deletePostDto.uuid().equals(TestDataSamples.POST_2_UUID) &&
            deletePostDto.authenticatedUser().id().equals(TestDataSamples.LEANNE_UUID) &&
            deletePostDto.authenticatedUser().username().equals(TestDataSamples.LEANNE_USERNAME)));
  }

  @Test
  void shouldDeletePostWhenAuthenticatedUserIsAdminButNotOwner() throws Exception {
    mockMvc.perform(delete("/api/posts/" + TestDataSamples.POST_2_UUID)
        .header("Authorization", "Bearer " + TestDataSamples.ADMIN_USERNAME))
        .andExpect(status().isNoContent());

    verify(deletePost, times(1))
        .delete(argThat(deletePostDto -> deletePostDto.uuid().equals(TestDataSamples.POST_2_UUID) &&
            deletePostDto.authenticatedUser().id().equals(TestDataSamples.ADMIN_UUID) &&
            deletePostDto.authenticatedUser().username().equals(TestDataSamples.ADMIN_USERNAME)));
  }

  @Test
  void shouldReturnNotFoundWhenPostUuidDoesNotExist() throws Exception {
    User user = UserFactory.createBasicUser();

    doThrow(new PostNotFound(NON_EXISTENT_POST_UUID)).when(deletePost)
        .delete(argThat(deletePostDto -> deletePostDto.uuid().equals(NON_EXISTENT_POST_UUID)));

    mockMvc.perform(delete("/api/posts/" + NON_EXISTENT_POST_UUID)
        .header("Authorization", "Bearer " + user.getUsername()))
        .andExpect(status().isNotFound());

    verify(deletePost, times(1)).delete(argThat(deletePostDto -> deletePostDto.uuid().equals(NON_EXISTENT_POST_UUID) &&
        deletePostDto.authenticatedUser().username().equals(user.getUsername())));
  }

  @Test
  void shouldReturnForbiddenWhenAuthenticatedUserIsNeitherOwnerNorAdmin() throws Exception {
    User nonOwnerUser = UserFactory.createBasicUser();
    var authenticatedUser = UserDetailsFactory.createAuthenticatedUserDto(nonOwnerUser);

    doThrow(new Forbidden("You are not the owner of this post")).when(deletePost)
        .delete(argThat(deletePostDto -> deletePostDto.uuid().equals(TestDataSamples.POST_2_UUID) &&
            deletePostDto.authenticatedUser().username().equals(authenticatedUser.username())));

    mockMvc.perform(delete("/api/posts/" + TestDataSamples.POST_2_UUID)
        .header("Authorization", "Bearer " + nonOwnerUser.getUsername()))
        .andExpect(status().isForbidden());

    verify(deletePost, times(1))
        .delete(argThat(deletePostDto -> deletePostDto.uuid().equals(TestDataSamples.POST_2_UUID) &&
            deletePostDto.authenticatedUser().username().equals(authenticatedUser.username())));
  }

  @Test
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
    mockMvc.perform(delete("/api/posts/" + TestDataSamples.POST_2_UUID))
        .andExpect(status().isUnauthorized());

    verify(deletePost, never()).delete(any());
  }
}
