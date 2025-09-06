package es.jmjg.experiments.infrastructure.controller.tag;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jmjg.experiments.application.tag.UpdateTagName;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagRequestDto;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class TagControllerPutTest extends BaseTagControllerTest {
  @Autowired
  private UpdateTagName updateTagName;

  private UUID testUuid;
  private Integer testId;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testId = 1;
    Tag testTag = TagFactory.createTag(testUuid, "test-tag");
    testTag.setId(testId);
    objectMapper = new ObjectMapper();
  }

  @Test
  void shouldUpdateTagName() throws Exception {
    // Given
    UpdateTagRequestDto tagDto = new UpdateTagRequestDto(testUuid, "updated-tag");
    Tag updatedTag = TagFactory.createTag(testUuid, "updated-tag");
    updatedTag.setId(testId);
    when(updateTagName.updateName(testUuid, "updated-tag")).thenReturn(updatedTag);

    // When & Then
    mockMvc.perform(put("/api/tags/{uuid}", testUuid)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(tagDto))
        .header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("updated-tag"));
  }
}
