package es.jmjg.experiments.infrastructure.controller.post;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

import es.jmjg.experiments.application.post.DeletePostDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.security.JwtUserDetails;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;

class PostControllerDeleteTest extends BasePostControllerTest {

  @Test
  void shouldDeletePostWhenGivenValidUUIDAndValidOwner() throws Exception {
    User user = UserFactory.createBasicUser();
    JwtUserDetails userDetails = UserFactory.createUserUserDetails(user);

    DeletePostDto deletePostDto = new DeletePostDto(TestDataSamples.POST_2_UUID, userDetails);
    doNothing().when(deletePost).deleteByUuid(deletePostDto);

    mockMvc.perform(delete("/api/posts/" + TestDataSamples.POST_2_UUID)
        .with(user(userDetails)))
        .andExpect(status().isNoContent());

    verify(deletePost, times(1)).deleteByUuid(deletePostDto);
  }
}
