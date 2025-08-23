package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.application.post.DeletePostDto;
import es.jmjg.experiments.application.post.exception.Forbidden;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.security.JwtUserDetailsService;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;

class PostControllerDeleteTest extends BasePostControllerTest {

  private static final UUID NON_EXISTENT_POST_UUID = UUID.randomUUID();

  @Test
  void shouldDeletePostWhenGivenValidUUIDAndValidOwner() throws Exception {
    User user = UserFactory.createUser(TestDataSamples.LEANNE_UUID, TestDataSamples.LEANNE_NAME,
        TestDataSamples.LEANNE_EMAIL, TestDataSamples.LEANNE_USERNAME);
    JwtUserDetails userDetails = UserFactory.createUserUserDetails(user);

    DeletePostDto deletePostDto = new DeletePostDto(TestDataSamples.POST_2_UUID, userDetails);

    mockMvc.perform(delete("/api/posts/" + TestDataSamples.POST_2_UUID)
        .with(user(userDetails)))
        .andExpect(status().isNoContent());

    verify(deletePost, times(1)).delete(deletePostDto);
  }

  @Test
  void shouldDeletePostWhenAuthenticatedUserIsAdminButNotOwner() throws Exception {
    User adminUser = UserFactory.createBasicUser();
    adminUser.setUsername(TestDataSamples.ADMIN_USERNAME);
    JwtUserDetails adminUserDetails = new JwtUserDetails(
        adminUser.getUuid(),
        adminUser.getUsername(),
        adminUser.getPassword(),
        java.util.List.of(
            new org.springframework.security.core.authority.SimpleGrantedAuthority(JwtUserDetailsService.ROLE_ADMIN)));

    DeletePostDto deletePostDto = new DeletePostDto(TestDataSamples.POST_2_UUID, adminUserDetails);
    doNothing().when(deletePost).delete(deletePostDto);

    mockMvc.perform(delete("/api/posts/" + TestDataSamples.POST_2_UUID)
        .with(user(adminUserDetails)))
        .andExpect(status().isNoContent());

    verify(deletePost, times(1)).delete(deletePostDto);
  }

  @Test
  void shouldReturnNotFoundWhenPostUuidDoesNotExist() throws Exception {
    User user = UserFactory.createBasicUser();
    JwtUserDetails userDetails = UserFactory.createUserUserDetails(user);

    DeletePostDto deletePostDto = new DeletePostDto(NON_EXISTENT_POST_UUID, userDetails);
    doThrow(new PostNotFound(NON_EXISTENT_POST_UUID)).when(deletePost).delete(deletePostDto);

    mockMvc.perform(delete("/api/posts/" + NON_EXISTENT_POST_UUID)
        .with(user(userDetails)))
        .andExpect(status().isNotFound());

    verify(deletePost, times(1)).delete(deletePostDto);
  }

  @Test
  void shouldReturnForbiddenWhenAuthenticatedUserIsNeitherOwnerNorAdmin() throws Exception {
    User nonOwnerUser = UserFactory.createBasicUser();
    JwtUserDetails nonOwnerUserDetails = UserFactory.createUserUserDetails(nonOwnerUser);

    DeletePostDto deletePostDto = new DeletePostDto(TestDataSamples.POST_2_UUID, nonOwnerUserDetails);
    doThrow(new Forbidden("You are not the owner of this post")).when(deletePost).delete(deletePostDto);

    mockMvc.perform(delete("/api/posts/" + TestDataSamples.POST_2_UUID)
        .with(user(nonOwnerUserDetails)))
        .andExpect(status().isForbidden());

    verify(deletePost, times(1)).delete(deletePostDto);
  }
}
