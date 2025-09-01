package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class FindUserByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserByUuid findUserByUuid;

  @Autowired
  private UserRepositoryImpl userRepository;

  private JwtUserDetails testUserDetails;
  private JwtUserDetails adminUserDetails;

  @BeforeEach
  void setUp() {
    User testUser = UserFactory.createBasicUser();
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);
    User adminUser = UserFactory.createAdminUser();
    adminUserDetails = UserDetailsFactory.createJwtUserDetails(adminUser);
  }

  @Test
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(TestDataSamples.LEANNE_UUID, adminUserDetails);
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(TestDataSamples.LEANNE_NAME);
    assertThat(result.get().getEmail()).isEqualTo(TestDataSamples.LEANNE_EMAIL);
    assertThat(result.get().getUsername()).isEqualTo(TestDataSamples.LEANNE_USERNAME);
    assertThat(result.get().getUuid()).isEqualTo(TestDataSamples.LEANNE_UUID);
    assertThat(result.get().getId()).isEqualTo(TestDataSamples.LEANNE_ID);
  }

  @Test
  void findByUuid_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(UUID.randomUUID(), adminUserDetails);
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuid_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // When
    FindUserByUuidDto findUserByUuidDto1 = new FindUserByUuidDto(TestDataSamples.ERVIN_UUID, adminUserDetails);
    FindUserByUuidDto findUserByUuidDto2 = new FindUserByUuidDto(TestDataSamples.CLEMENTINE_UUID, adminUserDetails);
    Optional<User> firstResult = findUserByUuid.findByUuid(findUserByUuidDto1);
    Optional<User> secondResult = findUserByUuid.findByUuid(findUserByUuidDto2);

    // Then
    assertThat(firstResult).isPresent();
    assertThat(firstResult.get().getName()).isEqualTo(TestDataSamples.ERVIN_NAME);
    assertThat(firstResult.get().getId()).isEqualTo(TestDataSamples.ERVIN_ID);

    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo(TestDataSamples.CLEMENTINE_NAME);
    assertThat(secondResult.get().getId()).isEqualTo(TestDataSamples.CLEMENTINE_ID);
  }

  @Test
  void findByUuid_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given - User is already created by Flyway migration
    User existingUser = userRepository.findByUuid(TestDataSamples.PATRICIA_UUID).orElseThrow();

    // Update the user
    existingUser.setName("Updated Patricia User");
    existingUser.setEmail("updated.patricia@example.com");
    userRepository.save(existingUser);

    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(TestDataSamples.PATRICIA_UUID, adminUserDetails);
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Updated Patricia User");
    assertThat(result.get().getEmail()).isEqualTo("updated.patricia@example.com");
    assertThat(result.get().getUsername()).isEqualTo(TestDataSamples.PATRICIA_USERNAME);
    assertThat(result.get().getUuid()).isEqualTo(TestDataSamples.PATRICIA_UUID);
  }

  @Test
  void findByUuid_WhenNonAdminUserTriesToAccessOtherUserData_ShouldThrowForbidden() {
    // Given
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(TestDataSamples.LEANNE_UUID, testUserDetails);

    // When & Then
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Access denied: only admins or the user themselves can view user data");
  }
}
