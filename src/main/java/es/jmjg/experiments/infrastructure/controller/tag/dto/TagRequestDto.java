package es.jmjg.experiments.infrastructure.controller.tag.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDto {
  @NotNull(message = "UUID is required")
  private UUID uuid;

  @NotEmpty(message = "Name is required")
  private String name;
}