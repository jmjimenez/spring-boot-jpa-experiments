package es.jmjg.experiments.application.user;

import es.jmjg.experiments.domain.user.exception.UserNotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByUsernameDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.repository.UserRepository;

@Service
public class FindUserByUsername {

  private final UserRepository userRepository;

  public FindUserByUsername(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public User findByUsername(FindUserByUsernameDto findUserByUsernameDto) {
    // Check if user is admin or if the authenticated user is requesting their own
    // data
    if (!findUserByUsernameDto.authenticatedUser().isAdmin() &&
        !findUserByUsernameDto.authenticatedUser().username().equals(findUserByUsernameDto.username())) {
      throw new Forbidden("Access denied: only admins or the user themselves can view user data");
    }

    return userRepository.findByUsername(findUserByUsernameDto.username()).orElseThrow(() -> new UserNotFound("User with username " + findUserByUsernameDto.username() + " not found"));
  }
}
