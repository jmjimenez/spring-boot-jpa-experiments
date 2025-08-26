package es.jmjg.experiments.application.user.dto;

import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindAllUsersDto {
  private Pageable pageable;
  private JwtUserDetails userDetails;
}
