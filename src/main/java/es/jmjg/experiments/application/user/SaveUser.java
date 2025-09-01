package es.jmjg.experiments.application.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class SaveUser {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public SaveUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User save(SaveUserDto saveUserDto) {
    if (!saveUserDto.userDetails().isAdmin()) {
      throw new Forbidden("Only administrators can create users");
    }

    User user = new User();
    user.setUuid(saveUserDto.uuid());
    user.setName(saveUserDto.name());
    user.setEmail(saveUserDto.email());
    user.setUsername(saveUserDto.username());
    user.setPassword(passwordEncoder.encode(saveUserDto.password()));

    return userRepository.save(user);
  }
}