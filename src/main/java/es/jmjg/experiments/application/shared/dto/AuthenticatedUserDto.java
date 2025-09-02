package es.jmjg.experiments.application.shared.dto;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetailsService;

public record AuthenticatedUserDto(
    UUID id,
        String username,
        String password,
        Collection<GrantedAuthority> authorities
) {

  public AuthenticatedUserDto {
    Objects.requireNonNull(id, "id cannot be null");
    Objects.requireNonNull(username, "username cannot be null");
    Objects.requireNonNull(password, "password cannot be null");
    Objects.requireNonNull(authorities, "authorities cannot be null");
  }

  public boolean isAdmin() {
    return authorities.contains(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_ADMIN));
  }
}
