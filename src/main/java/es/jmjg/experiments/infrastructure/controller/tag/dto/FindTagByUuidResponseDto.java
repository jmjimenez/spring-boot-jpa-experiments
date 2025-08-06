package es.jmjg.experiments.infrastructure.controller.tag.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindTagByUuidResponseDto {
  private UUID uuid;
  private String name;
  private List<UUID> posts;
  private List<UUID> users;
}