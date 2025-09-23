package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.application.user.dto.UpdateUserDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;

class UpdateUserIntegrationTest extends BaseIntegration {

  @Autowired
  private UpdateUser updateUser;

  @Autowired
  private UserRepositoryImpl userRepository;

  private AuthenticatedUserDto authenticatedAdminUser;

  @BeforeEach
  void setUp() {
    User adminUser = UserFactory.createUser("Admin User", "admin@example.com", "admin");
    authenticatedAdminUser = AuthenticatedUserFactory.createAuthenticatedUserDto(adminUser);
  }

  @Test
  void update_WhenUserExists_ShouldUpdateFields() {
    UUID leanneUuid = TestDataSamples.LEANNE_UUID;

    UpdateUserDto updateUserDto = new UpdateUserDto(
        leanneUuid,
        "Updated Ervin",
        "updatedervin@example.com",
        authenticatedAdminUser);

    User result = updateUser.update(updateUserDto);

    assertThat(result.getName()).isEqualTo("Updated Ervin");
    assertThat(result.getEmail()).isEqualTo("updatedervin@example.com");
    assertThat(result.getUuid()).isEqualTo(leanneUuid);

    Optional<User> dbUser = userRepository.findByUuid(leanneUuid);
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getName()).isEqualTo("Updated Ervin");
    assertThat(dbUser.get().getEmail()).isEqualTo("updatedervin@example.com");
  }

  @Test
  void update_WhenUserDoesNotExist_ShouldThrow() {
    UpdateUserDto updateUserDto = new UpdateUserDto(
        UUID.randomUUID(),
        "Updated Name",
        "updated@example.com",
        authenticatedAdminUser);

    assertThatThrownBy(() -> updateUser.update(updateUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found with id: " + updateUserDto.uuid());
  }
}
