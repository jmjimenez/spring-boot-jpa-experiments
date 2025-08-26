package es.jmjg.experiments.application.user.dto;

import java.util.UUID;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserDto {
  private UUID uuid;
  private JwtUserDetails userDetails;
}
