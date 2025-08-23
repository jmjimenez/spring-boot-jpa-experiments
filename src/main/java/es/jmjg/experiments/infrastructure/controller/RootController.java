package es.jmjg.experiments.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden
public class RootController {

  @GetMapping("/")
  public ResponseEntity<Void> getRoot() {
    return ResponseEntity.status(HttpStatus.FOUND)
        .header("Location", "/swagger-ui/index.html")
        .build();
  }
}
