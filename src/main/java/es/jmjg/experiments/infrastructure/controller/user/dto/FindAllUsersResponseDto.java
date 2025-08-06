package es.jmjg.experiments.infrastructure.controller.user.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindAllUsersResponseDto {
  private UUID uuid;
  private String name;
  private String email;
  private String username;
  private List<UUID> posts;
  private List<String> tags;
}
