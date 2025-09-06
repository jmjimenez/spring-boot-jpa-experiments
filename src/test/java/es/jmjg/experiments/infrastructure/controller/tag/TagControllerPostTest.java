package es.jmjg.experiments.infrastructure.controller.tag;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jmjg.experiments.application.tag.SaveTag;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.controller.tag.dto.SaveTagRequestDto;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class TagControllerPostTest extends BaseTagControllerTest {
  @Autowired
  private SaveTag saveTag;

  private Tag testTag;
  private UUID testUuid;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    Integer testId = 1;
    testTag = TagFactory.createTag(testUuid, "test-tag");
    testTag.setId(testId);
    objectMapper = new ObjectMapper();
  }

  @Test
  void shouldSaveTag() throws Exception {
    // Given
    SaveTagRequestDto tagDto = new SaveTagRequestDto(testUuid, "new-tag");
    when(saveTag.save(any())).thenReturn(testTag);

    // When & Then
    mockMvc.perform(post("/api/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(tagDto))
        .header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/tags/" + testUuid.toString()))
        .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
        .andExpect(jsonPath("$.name").value("test-tag"));
  }
}
