package es.jmjg.experiments.infrastructure.controller.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResponseDto {
  private UUID uuid;
  private String name;
  private List<UUID> posts;
  private List<UUID> users;
}