package es.jmjg.experiments.infrastructure.controller.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.jmjg.experiments.domain.post.entity.PostUuid;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new post")
public class SavePostRequestDto {
  @NotNull(message = "Id is required")
  @Schema(description = "Identifier for the post", example = "123e4567-e89b-12d3-a456-426614174000")
  @JsonProperty("post-id")
  private PostUuid id;

  @NotEmpty(message = "Title is required")
  @Schema(description = "Title of the post", example = "My First Post")
  private String title;

  @NotEmpty(message = "Body is required")
  @Schema(description = "Content body of the post", example = "This is the content of my first post.")
  private String body;

  @Schema(description = "List of tag names to associate with the post", example = "[\"java\", \"spring-boot\"]")
  @JsonProperty("tag-names")
  private List<String> tagNames;
}
