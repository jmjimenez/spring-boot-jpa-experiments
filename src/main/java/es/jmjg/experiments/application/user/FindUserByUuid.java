package es.jmjg.experiments.application.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    return userRepository.findByUuid(findUserByUuidDto.uuid());
  }
}