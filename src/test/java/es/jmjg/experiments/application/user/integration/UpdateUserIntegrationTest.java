package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.UserFactory;

class UpdateUserIntegrationTest extends BaseIntegration {

  @Autowired
  private UpdateUser updateUser;

  @Autowired
  private UserRepositoryImpl userRepository;

  private User existingUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    existingUser = userRepository.save(UserFactory.createBasicUser());
  }

  @Test
  void update_WhenUserExists_ShouldUpdateFields() {
    User updateData = UserFactory.createUser("Updated Name", "updated@example.com", "updateduser");
    updateData.setUuid(null);

    User result = updateUser.update(existingUser.getId(), updateData);

    assertThat(result.getName()).isEqualTo("Updated Name");
    assertThat(result.getEmail()).isEqualTo("updated@example.com");
    assertThat(result.getUsername()).isEqualTo("updateduser");
    assertThat(result.getUuid()).isEqualTo(existingUser.getUuid());

    Optional<User> dbUser = userRepository.findById(existingUser.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getName()).isEqualTo("Updated Name");
  }

  @Test
  void update_WhenUserExistsAndUuidProvided_ShouldUpdateUuid() {
    UUID newUuid = UUID.randomUUID();
    User updateData = UserFactory.createUser("Updated Name", "updated@example.com", "updateduser");
    updateData.setUuid(newUuid);

    User result = updateUser.update(existingUser.getId(), updateData);

    assertThat(result.getUuid()).isEqualTo(newUuid);
    Optional<User> dbUser = userRepository.findById(existingUser.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getUuid()).isEqualTo(newUuid);
  }

  @Test
  void update_WhenUserDoesNotExist_ShouldThrow() {
    User updateData = UserFactory.createUser("Updated Name", "updated@example.com", "updateduser");
    updateData.setUuid(null);
    int nonExistentId = 9999;
    assertThatThrownBy(() -> updateUser.update(nonExistentId, updateData))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found with id: " + nonExistentId);
  }
}