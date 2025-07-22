package es.jmjg.experiments.infrastructure.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.jmjg.experiments.application.tag.DeleteTagByUuid;
import es.jmjg.experiments.application.tag.FindPostsByTag;
import es.jmjg.experiments.application.tag.FindTagByPattern;
import es.jmjg.experiments.application.tag.FindUsersByTag;
import es.jmjg.experiments.application.tag.SaveTag;
import es.jmjg.experiments.application.tag.UpdateTagName;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.ControllerTestConfig;
import es.jmjg.experiments.infrastructure.controller.dto.TagRequestDto;
import es.jmjg.experiments.shared.PostFactory;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.UserFactory;

@WebMvcTest(TagController.class)
@Import(ControllerTestConfig.class)
class TagControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SaveTag saveTag;

  @Autowired
  private UpdateTagName updateTagName;

  @Autowired
  private DeleteTagByUuid deleteTagByUuid;

  @Autowired
  private FindTagByPattern findTagByPattern;

  @Autowired
  private FindUsersByTag findUsersByTag;

  @Autowired
  private FindPostsByTag findPostsByTag;

  private Tag testTag;
  private UUID testUuid;
  private Integer testId;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testId = 1;
    testTag = TagFactory.createTag(testUuid, "test-tag");
    testTag.setId(testId);
    objectMapper = new ObjectMapper();
  }

  @Test
  void shouldFindTagsByPattern() throws Exception {
    // Given
    String pattern = "test";
    List<Tag> tags = List.of(testTag);
    when(findTagByPattern.findByPattern(pattern)).thenReturn(tags);

    // When & Then
    mockMvc.perform(get("/api/tags/search")
        .param("pattern", pattern))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].uuid").value(testUuid.toString()))
        .andExpect(jsonPath("$[0].name").value("test-tag"));
  }

  @Test
  void shouldFindUsersByTag() throws Exception {
    // Given
    User user = UserFactory.createUser("Test User", "test@example.com", "testuser");
    List<User> users = List.of(user);
    when(findUsersByTag.findByTagUuid(testUuid)).thenReturn(users);

    // When & Then
    mockMvc.perform(get("/api/tags/{uuid}/users", testUuid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Test User"))
        .andExpect(jsonPath("$[0].email").value("test@example.com"));
  }

  @Test
  void shouldFindPostsByTag() throws Exception {
    // Given
    User user = UserFactory.createUser("Test User", "test@example.com", "testuser");
    Post post = PostFactory.createPost(user, UUID.randomUUID(), "Test Post", "Test content");
    List<Post> posts = List.of(post);
    when(findPostsByTag.findByTagUuid(testUuid)).thenReturn(posts);

    // When & Then
    mockMvc.perform(get("/api/tags/{uuid}/posts", testUuid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Test Post"))
        .andExpect(jsonPath("$[0].body").value("Test content"));
  }

  @Test
  void shouldFindUsersByTagName() throws Exception {
    // Given
    User user = UserFactory.createUser("Test User", "test@example.com", "testuser");
    List<User> users = List.of(user);
    when(findUsersByTag.findByTagName("test-tag")).thenReturn(users);

    // When & Then
    mockMvc.perform(get("/api/tags/search/users")
        .param("name", "test-tag"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Test User"));
  }

  @Test
  void shouldFindPostsByTagName() throws Exception {
    // Given
    User user = UserFactory.createUser("Test User", "test@example.com", "testuser");
    Post post = PostFactory.createPost(user, UUID.randomUUID(), "Test Post", "Test content");
    List<Post> posts = List.of(post);
    when(findPostsByTag.findByTagName("test-tag")).thenReturn(posts);

    // When & Then
    mockMvc.perform(get("/api/tags/search/posts")
        .param("name", "test-tag"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Test Post"));
  }

  @Test
  void shouldSaveTag() throws Exception {
    // Given
    TagRequestDto tagDto = new TagRequestDto(testUuid, "new-tag");
    when(saveTag.save(any(Tag.class))).thenReturn(testTag);

    // When & Then
    mockMvc.perform(post("/api/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(tagDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
        .andExpect(jsonPath("$.name").value("test-tag"));
  }

  @Test
  void shouldUpdateTagName() throws Exception {
    // Given
    TagRequestDto tagDto = new TagRequestDto(testUuid, "updated-tag");
    Tag updatedTag = TagFactory.createTag(testUuid, "updated-tag");
    updatedTag.setId(testId);
    when(updateTagName.updateName(testUuid, "updated-tag")).thenReturn(updatedTag);

    // When & Then
    mockMvc.perform(put("/api/tags/{uuid}", testUuid)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(tagDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("updated-tag"));
  }

  @Test
  void shouldDeleteTag() throws Exception {
    // Given
    doNothing().when(deleteTagByUuid).deleteByUuid(testUuid);

    // When & Then
    mockMvc.perform(delete("/api/tags/{uuid}", testUuid))
        .andExpect(status().isNoContent());
  }
}