package es.jmjg.experiments.application.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
  private Integer id;
  private UUID uuid;
  private String name;
  private String email;
  private String username;
  private String password;
}
