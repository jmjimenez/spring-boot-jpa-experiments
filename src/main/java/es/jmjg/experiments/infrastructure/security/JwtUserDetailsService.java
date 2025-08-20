package es.jmjg.experiments.infrastructure.security;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
  public static final String ROLE_USER = "ROLE_USER";

  private final UserRepositoryImpl userRepository;

  @Override
  public UserDetails loadUserByUsername(final String username) {
    final Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      throw new UsernameNotFoundException("User " + username + " not found");
    }

    final User user = userOpt.get();
    final List<SimpleGrantedAuthority> roles = List.of(new SimpleGrantedAuthority(ROLE_USER));

    return new JwtUserDetails(user.getUuid(), username, user.getPassword(), roles);
  }
}
