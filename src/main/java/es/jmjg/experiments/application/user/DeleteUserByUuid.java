package es.jmjg.experiments.application.user;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class DeleteUserByUuid {

  private final UserRepository userRepository;

  public DeleteUserByUuid(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public void deleteByUuid(UUID uuid) {
    userRepository.deleteByUuid(uuid);
  }
}
