package es.jmjg.experiments.application.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByUsernameDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class FindUserByUsername {

  private final UserRepository userRepository;

  public FindUserByUsername(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public Optional<User> findByUsername(FindUserByUsernameDto findUserByUsernameDto) {
    // Check if user is admin or if the authenticated user is requesting their own
    // data
    if (!findUserByUsernameDto.authenticatedUser().isAdmin() &&
        !findUserByUsernameDto.authenticatedUser().username().equals(findUserByUsernameDto.username())) {
      throw new Forbidden("Access denied: only admins or the user themselves can view user data");
    }

    var user = userRepository.findByUsername(findUserByUsernameDto.username());
    return user;
  }
}