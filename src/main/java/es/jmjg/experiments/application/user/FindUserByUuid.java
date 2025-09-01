package es.jmjg.experiments.application.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class FindUserByUuid {

  private final UserRepository userRepository;

  public FindUserByUuid(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public Optional<User> findByUuid(FindUserByUuidDto findUserByUuidDto) {
    // Check if user is admin or if the authenticated user is requesting their own
    // data
    if (!findUserByUuidDto.userDetails().isAdmin() &&
        !findUserByUuidDto.userDetails().id.equals(findUserByUuidDto.uuid())) {
      throw new Forbidden("Access denied: only admins or the user themselves can view user data");
    }

    return userRepository.findByUuid(findUserByUuidDto.uuid());
  }
}