package es.jmjg.experiments.application.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.user.dto.UpdateUserDto;
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
    Optional<User> existing = userRepository.findById(updateUserDto.getId());
    if (existing.isPresent()) {
      User existingUser = existing.get();
      existingUser.setName(updateUserDto.getName());
      existingUser.setEmail(updateUserDto.getEmail());
      existingUser.setUsername(updateUserDto.getUsername());
      if (updateUserDto.getPassword() != null) {
        existingUser.setPassword(updateUserDto.getPassword());
      }
      if (updateUserDto.getUuid() != null) {
        existingUser.setUuid(updateUserDto.getUuid());
      }
      return userRepository.save(existingUser);
    } else {
      throw new RuntimeException("User not found with id: " + updateUserDto.getId());
    }
  }
}