package es.jmjg.experiments.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Root", description = "Root endpoint operations")
public class RootController {

  @GetMapping("/")
  @Operation(summary = "Get API information", description = "Returns basic information about the Spring Boot JPA Experiments API")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved API information")
  })
  public ResponseEntity<String> getRoot() {
    return ResponseEntity.ok("Spring Boot JPA Experiments API - Use /swagger-ui.html for API documentation");
  }
}
