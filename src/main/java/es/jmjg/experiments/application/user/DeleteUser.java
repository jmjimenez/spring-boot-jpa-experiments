package es.jmjg.experiments.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.user.dto.DeleteUserDto;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class DeleteUser {

  private final UserRepository userRepository;

  public DeleteUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public void deleteByUuid(DeleteUserDto deleteUserDto) {
    userRepository.deleteByUuid(deleteUserDto.getUuid());
  }
}
