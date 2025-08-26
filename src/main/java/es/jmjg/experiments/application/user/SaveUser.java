package es.jmjg.experiments.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.user.dto.SaveUserDto;
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
    user.setUuid(saveUserDto.uuid());
    user.setName(saveUserDto.name());
    user.setEmail(saveUserDto.email());
    user.setUsername(saveUserDto.username());
    user.setPassword(saveUserDto.password());

    return userRepository.save(user);
  }
}