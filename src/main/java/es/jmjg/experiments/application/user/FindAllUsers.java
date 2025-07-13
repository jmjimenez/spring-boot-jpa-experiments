package es.jmjg.experiments.application.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@Service
public class FindAllUsers {

  private final UserRepository userRepository;

  public FindAllUsers(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userRepository.findAll();
  }
}