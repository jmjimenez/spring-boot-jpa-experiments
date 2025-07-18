package es.jmjg.experiments.application.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@Service
public class UpdateUser {

  private final UserRepository userRepository;

  public UpdateUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public User update(Integer id, User user) {
    Optional<User> existing = userRepository.findById(id);
    if (existing.isPresent()) {
      User existingUser = existing.get();
      existingUser.setName(user.getName());
      existingUser.setEmail(user.getEmail());
      existingUser.setUsername(user.getUsername());
      if (user.getUuid() != null) {
        existingUser.setUuid(user.getUuid());
      }
      return userRepository.save(existingUser);
    } else {
      throw new RuntimeException("User not found with id: " + id);
    }
  }
}