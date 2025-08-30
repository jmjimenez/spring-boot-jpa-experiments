package es.jmjg.experiments.application.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.UpdateUserDto;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class UpdateUser {

  private final UserRepository userRepository;

  public UpdateUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public User update(UpdateUserDto updateUserDto) {
    Optional<User> existing = userRepository.findByUuid(updateUserDto.uuid());
    if (!existing.isPresent()) {
      throw new UserNotFound(updateUserDto.uuid());
    }

    if (!updateUserDto.userDetails().isAdmin() &&
        !updateUserDto.userDetails().id.equals(updateUserDto.uuid())) {
      throw new Forbidden("Access denied: only admins or the user themselves can update user data");
    }

    User existingUser = existing.get();
    existingUser.setName(updateUserDto.name());
    existingUser.setEmail(updateUserDto.email());
    return userRepository.save(existingUser);
  }
}