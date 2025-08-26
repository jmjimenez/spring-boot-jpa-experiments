package es.jmjg.experiments.application.user;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.user.dto.FindAllUsersDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class FindAllUsers {

  private final UserRepository userRepository;

  public FindAllUsers(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public Page<User> findAll(FindAllUsersDto findAllUsersDto) {
    return userRepository.findAll(findAllUsersDto.getPageable());
  }
}