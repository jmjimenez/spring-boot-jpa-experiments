package es.jmjg.experiments.infrastructure.controller.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//TODO: each entry point should have its own DTO (see
// https://www.wimdeblauwe.com/blog/2025/07/30/how-i-test-production-ready-spring-boot-applications/)
public class PostRequestDto {
  @NotNull(message = "UUID is required")
  private UUID uuid;

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotEmpty(message = "Title is required")
  private String title;

  @NotEmpty(message = "Body is required")
  private String body;

  private List<String> tagNames;
}
