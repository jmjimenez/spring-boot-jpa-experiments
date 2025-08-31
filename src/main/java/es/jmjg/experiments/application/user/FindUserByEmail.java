package es.jmjg.experiments.application.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByEmailDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class FindUserByEmail {

  private final UserRepository userRepository;

  public FindUserByEmail(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public Optional<User> findByEmail(FindUserByEmailDto findUserByEmailDto) {
    if (!findUserByEmailDto.userDetails().isAdmin()) {
      throw new Forbidden("Only admin users can search users by email");
    }

    return userRepository.findByEmail(findUserByEmailDto.email());
  }
}