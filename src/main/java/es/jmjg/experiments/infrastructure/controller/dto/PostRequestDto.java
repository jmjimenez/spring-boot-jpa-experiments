package es.jmjg.experiments.infrastructure.controller.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
  private Integer id;

  @NotNull(message = "UUID is required")
  private UUID uuid;

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotEmpty(message = "Title is required")
  private String title;

  @NotEmpty(message = "Body is required")
  private String body;
}
