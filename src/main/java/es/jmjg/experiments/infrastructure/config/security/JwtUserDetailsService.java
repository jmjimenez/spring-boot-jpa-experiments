package es.jmjg.experiments.infrastructure.config.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.AppProperties;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
  public static final String ROLE_USER = "ROLE_USER";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  private final UserRepositoryImpl userRepository;
  private final AppProperties appProperties;

  @Override
  public UserDetails loadUserByUsername(final String username) {
    final Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      throw new UsernameNotFoundException("User " + username + " not found");
    }

    final User user = userOpt.get();
    final List<SimpleGrantedAuthority> roles = new ArrayList<>();

    // All users get ROLE_USER
    roles.add(new SimpleGrantedAuthority(ROLE_USER));

    // Admin users also get ROLE_ADMIN
    if (username.equals(appProperties.getAdminUsername())) {
      roles.add(new SimpleGrantedAuthority(ROLE_ADMIN));
    }

    return new JwtUserDetails(user.getUuid(), username, user.getPassword(), roles);
  }
}
