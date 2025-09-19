package es.jmjg.experiments.application.post.dto;

import java.util.Objects;
import org.springframework.data.domain.Pageable;

public record FindAllPostsDto(
  Pageable pageable
) {

  public FindAllPostsDto {
    Objects.requireNonNull(pageable, "pageable cannot be null");
  }
}
