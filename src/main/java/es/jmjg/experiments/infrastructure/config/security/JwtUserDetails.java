package es.jmjg.experiments.infrastructure.config.security;

import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {
  public final UserUuid id;

  public JwtUserDetails(
      final UserUuid id,
      final String username,
      final String password,
      final Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.id = id;
  }

  public boolean isAdmin() {
    return getAuthorities().contains(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_ADMIN));
  }
}
