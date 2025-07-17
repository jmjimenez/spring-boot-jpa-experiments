package es.jmjg.experiments.application.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }
}