package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.application.user.dto.UpdateUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class UpdateUserIntegrationTest extends BaseIntegration {

  @Autowired
  private UpdateUser updateUser;

  @Autowired
  private UserRepositoryImpl userRepository;

  private User leanneUser;
  private User ervinUser;
  private JwtUserDetails testUserDetails;

  @BeforeEach
  void setUp() {
    leanneUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID)
        .orElseThrow(() -> new RuntimeException("Test user not found: " + TestDataSamples.LEANNE_UUID));

    ervinUser = userRepository.findByUuid(TestDataSamples.ERVIN_UUID)
        .orElseThrow(() -> new RuntimeException("Test user not found: " + TestDataSamples.ERVIN_UUID));

    User testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);
  }

  @Test
  void update_WhenUserExists_ShouldUpdateFields() {
    UpdateUserDto updateUserDto = new UpdateUserDto(
        leanneUser.getId(),
        null,
        "Updated Ervin",
        "updatedervin@example.com",
        "updatedervin",
        null,
        testUserDetails);

    User result = updateUser.update(updateUserDto);

    assertThat(result.getName()).isEqualTo("Updated Ervin");
    assertThat(result.getEmail()).isEqualTo("updatedervin@example.com");
    assertThat(result.getUsername()).isEqualTo("updatedervin");
    assertThat(result.getUuid()).isEqualTo(leanneUser.getUuid());

    Optional<User> dbUser = userRepository.findById(leanneUser.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getName()).isEqualTo("Updated Ervin");
  }

  @Test
  void update_WhenUserExistsAndUuidProvided_ShouldUpdateUuid() {
    UUID newUuid = UUID.randomUUID();
    UpdateUserDto updateUserDto = new UpdateUserDto(
        ervinUser.getId(),
        newUuid,
        "Updated Name",
        "updated@example.com",
        "updateduser",
        null,
        testUserDetails);

    User result = updateUser.update(updateUserDto);

    assertThat(result.getUuid()).isEqualTo(newUuid);
    Optional<User> dbUser = userRepository.findById(ervinUser.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getUuid()).isEqualTo(newUuid);
  }

  @Test
  void update_WhenUserDoesNotExist_ShouldThrow() {
    UpdateUserDto updateUserDto = new UpdateUserDto(
        9999,
        null,
        "Updated Name",
        "updated@example.com",
        "updateduser",
        null,
        testUserDetails);

    assertThatThrownBy(() -> updateUser.update(updateUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found with id: 9999");
  }
}