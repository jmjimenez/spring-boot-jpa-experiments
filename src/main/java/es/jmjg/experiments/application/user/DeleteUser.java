package es.jmjg.experiments.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.DeleteUserDto;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class DeleteUser {

  private final UserRepository userRepository;

  public DeleteUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public void delete(DeleteUserDto deleteUserDto) {
    if (!deleteUserDto.userDetails().isAdmin()) {
      throw new Forbidden("Only admin users can delete users");
    }

    var user = userRepository.findByUuid(deleteUserDto.uuid());

    if (user.isEmpty()) {
      throw new UserNotFound(deleteUserDto.uuid());
    }

    userRepository.deleteByUuid(deleteUserDto.uuid());
  }
}
