package es.jmjg.experiments.application.user.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserDto {
  private UUID uuid;
  private String name;
  private String email;
  private String username;
  private String password;
}
