package es.jmjg.experiments.application.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@Service
public class FindUserById {

  private final UserRepository userRepository;

  public FindUserById(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public Optional<User> findById(Integer id) {
    return userRepository.findById(id);
  }
}