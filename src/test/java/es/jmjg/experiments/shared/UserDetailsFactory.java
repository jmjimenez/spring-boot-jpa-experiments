package es.jmjg.experiments.shared;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetailsService;

public class UserDetailsFactory {

  public static JwtUserDetails createJwtUserDetails(User user) {
    return new JwtUserDetails(
        user.getUuid(),
        user.getUsername(),
        user.getPassword(),
        user.getUsername().equals(TestDataSamples.ADMIN_USERNAME)
            ? List.of(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_USER),
                new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_ADMIN))
            : List.of(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_USER)));
  }
}
