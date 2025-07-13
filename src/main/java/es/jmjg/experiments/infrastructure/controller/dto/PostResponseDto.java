package es.jmjg.experiments.infrastructure.controller.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
  private Integer id;
  private UUID uuid;
  private UUID userId;
  private String title;
  private String body;
}
