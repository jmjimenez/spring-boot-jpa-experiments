package es.jmjg.experiments.infrastructure.controller.post.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//TODO: this class only needs getters not setters
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for updating tags of an existing post")
public class UpdatePostTagsRequestDto {
    @NotEmpty(message = "Tag names are required")
    @Schema(description = "List of tag names to associate with the post", example = "[\"java\", \"spring-boot\", \"updated\"]")
    private List<String> tagNames;
}
