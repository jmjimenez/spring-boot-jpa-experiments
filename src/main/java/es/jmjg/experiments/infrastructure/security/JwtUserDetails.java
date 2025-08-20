package es.jmjg.experiments.infrastructure.security;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {
  public final UUID id;

  public JwtUserDetails(
      final UUID id,
      final String username,
      final String password,
      final Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.id = id;
  }
}
