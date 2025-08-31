package es.jmjg.experiments.infrastructure.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import es.jmjg.experiments.application.user.dto.FindAllUsersDto;
import es.jmjg.experiments.domain.entity.User;

class UserControllerGetFindAllTest extends BaseUserControllerTest {

  @Test
  void shouldFindAllUsers() throws Exception {
    // Given
    List<User> users = List.of(testUser);
    Page<User> userPage = new PageImpl<>(users, pageable, users.size());
    when(findAllUsers.findAll(any(FindAllUsersDto.class))).thenReturn(userPage);

    String expectedJson = createFindAllUsersJsonResponse();

    // When & Then
    mockMvc
        .perform(get("/api/users").header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
    // When & Then
    mockMvc
        .perform(get("/api/users"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnEmptyListWhenNoUsersExist() throws Exception {
    // Given
    List<User> emptyUsers = List.of();
    Page<User> emptyUserPage = new PageImpl<>(emptyUsers, pageable, 0);
    when(findAllUsers.findAll(any(FindAllUsersDto.class))).thenReturn(emptyUserPage);

    String expectedEmptyJson = """
        {
          "content": [],
          "pageable": {
            "sort": {
              "empty": true,
              "sorted": false,
              "unsorted": true
            },
            "offset": 0,
            "pageNumber": 0,
            "pageSize": 10,
            "paged": true,
            "unpaged": false
          },
          "last": true,
          "totalElements": 0,
          "totalPages": 0,
          "size": 10,
          "number": 0,
          "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
          },
          "numberOfElements": 0,
          "first": true,
          "empty": true
        }""";

    // When & Then
    mockMvc
        .perform(get("/api/users").header("Authorization", "Bearer " + adminUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedEmptyJson));
  }

}
