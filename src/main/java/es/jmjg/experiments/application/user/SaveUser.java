package es.jmjg.experiments.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class SaveUser {

  private final UserRepository userRepository;

  public SaveUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public User save(SaveUserDto saveUserDto) {
    User user = new User();
    user.setUuid(saveUserDto.getUuid());
    user.setName(saveUserDto.getName());
    user.setEmail(saveUserDto.getEmail());
    user.setUsername(saveUserDto.getUsername());
    user.setPassword(saveUserDto.getPassword());

    return userRepository.save(user);
  }
}