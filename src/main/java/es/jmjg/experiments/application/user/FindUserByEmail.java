package es.jmjg.experiments.application.user;

import es.jmjg.experiments.domain.user.exception.UserNotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByEmailDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.repository.UserRepository;

@Service
public class FindUserByEmail {

  private final UserRepository userRepository;

  public FindUserByEmail(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public User findByEmail(FindUserByEmailDto findUserByEmailDto) {
    if (!findUserByEmailDto.authenticatedUser().isAdmin()) {
      throw new Forbidden("Only admin users can search users by email");
    }

    return userRepository.findByEmail(findUserByEmailDto.email()).orElseThrow(() -> new UserNotFound("User with email " + findUserByEmailDto.email() + " not found"));
  }
}
