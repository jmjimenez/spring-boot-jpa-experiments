package es.jmjg.experiments.infrastructure.controller.user;

import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import es.jmjg.experiments.infrastructure.config.ControllerTestConfig;

@WebMvcTest(UserController.class)
@Import(ControllerTestConfig.class)
abstract class BaseUserControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  protected void verifyJsonResponse(ResultActions resultActions, String expectedJson) throws Exception {
    JSONAssert.assertEquals(
        expectedJson, resultActions.andReturn().getResponse().getContentAsString(), false);
  }
}
