package es.jmjg.experiments.application.user;

import es.jmjg.experiments.domain.user.exception.UserNotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.repository.UserRepository;

@Service
public class FindUserByUuid {

  private final UserRepository userRepository;

  public FindUserByUuid(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public User findByUuid(FindUserByUuidDto findUserByUuidDto) {
    if (!findUserByUuidDto.authenticatedUser().isAdmin() &&
        !findUserByUuidDto.authenticatedUser().id().equals(findUserByUuidDto.uuid())) {
      throw new Forbidden("Access denied: only admins or the user themselves can view user data");
    }

    return userRepository.findByUuid(findUserByUuidDto.uuid()).orElseThrow(() -> new UserNotFound("user with uuid " + findUserByUuidDto.uuid() + " not found"));
  }
}
