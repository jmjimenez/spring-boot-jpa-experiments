package es.jmjg.experiments.infrastructure.controller.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
  private UUID uuid;
  private String name;
  private String email;
  private String username;
}
