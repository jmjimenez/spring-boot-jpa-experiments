package es.jmjg.experiments.infrastructure.controller.tag;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jmjg.experiments.application.tag.UpdateTag;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.infrastructure.controller.tag.dto.UpdateTagRequestDto;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class TagControllerPutTest extends BaseTagControllerTest {
  @Autowired
  private UpdateTag updateTag;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  void shouldUpdateTagName() throws Exception {
    // Given
    String updatedName = "updated-tag";
    Tag tag = TagFactory.createTag("original-tag");
    Tag updatedTag = new Tag(tag.getId(), tag.getUuid(), updatedName);
    when(updateTag.update(any())).thenReturn(updatedTag);

    // When & Then
    UpdateTagRequestDto tagDto = new UpdateTagRequestDto(tag.getUuid(), updatedName);
    mockMvc.perform(put("/api/tags/{uuid}", tag.getUuid())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(tagDto))
        .header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(updatedName));
  }
}
