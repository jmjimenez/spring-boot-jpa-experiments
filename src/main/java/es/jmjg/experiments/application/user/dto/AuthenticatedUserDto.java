package es.jmjg.experiments.application.user.dto;

import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import es.jmjg.experiments.infrastructure.config.security.JwtUserDetailsService;

public record AuthenticatedUserDto(
  UserUuid id,
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
