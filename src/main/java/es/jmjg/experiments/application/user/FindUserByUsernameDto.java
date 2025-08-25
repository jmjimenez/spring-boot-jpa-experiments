package es.jmjg.experiments.application.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindUserByUsernameDto {
  private String username;
}
