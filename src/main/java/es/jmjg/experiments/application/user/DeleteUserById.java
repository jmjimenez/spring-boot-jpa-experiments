package es.jmjg.experiments.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.infrastructure.repository.UserRepository;

@Service
public class DeleteUserById {

  private final UserRepository userRepository;

  public DeleteUserById(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public void deleteById(Integer id) {
    userRepository.deleteById(id);
  }
}