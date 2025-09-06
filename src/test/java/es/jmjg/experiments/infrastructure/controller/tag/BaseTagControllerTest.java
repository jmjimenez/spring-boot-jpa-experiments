package es.jmjg.experiments.infrastructure.controller.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import es.jmjg.experiments.infrastructure.config.ControllerTestConfig;

@WebMvcTest(TagController.class)
@Import(ControllerTestConfig.class)
abstract class BaseTagControllerTest {

  @Autowired
  protected MockMvc mockMvc;
}
