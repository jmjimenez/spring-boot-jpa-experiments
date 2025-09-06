package es.jmjg.experiments.infrastructure.controller.tag;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.jmjg.experiments.application.tag.DeleteTag;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.shared.TagFactory;
import es.jmjg.experiments.shared.TestDataSamples;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TagControllerDeleteTest extends BaseTagControllerTest {
  @Autowired
  private DeleteTag deleteTag;

  private UUID testUuid;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    Integer testId = 1;
    Tag testTag = TagFactory.createTag(testUuid, "test-tag");
    testTag.setId(testId);
  }

  @Test
  void shouldDeleteTag() throws Exception {
    // Given
    doNothing().when(deleteTag).delete(any());

    // When & Then
    mockMvc.perform(delete("/api/tags/{uuid}", testUuid)
        .header("Authorization", "Bearer " + TestDataSamples.LEANNE_USERNAME))
        .andExpect(status().isNoContent());
  }
}
