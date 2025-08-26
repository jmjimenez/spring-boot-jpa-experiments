package es.jmjg.experiments.application.user.dto;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindUserByEmailDto {
  private String email;
  private JwtUserDetails userDetails;
}
