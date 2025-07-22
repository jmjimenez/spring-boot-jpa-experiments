package es.jmjg.experiments.infrastructure.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TagNotFoundException extends RuntimeException {

  public TagNotFoundException() {
    super("Tag not found");
  }

  public TagNotFoundException(String message) {
    super(message);
  }
}