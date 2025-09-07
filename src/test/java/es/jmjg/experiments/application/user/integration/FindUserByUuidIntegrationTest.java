package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import es.jmjg.experiments.domain.user.exception.UserNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;

class FindUserByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserByUuid findUserByUuid;

  @Autowired
  private UserRepositoryImpl userRepository;

  private AuthenticatedUserDto authenticatedTestUser;
  private AuthenticatedUserDto authenticatedAdminUser;

  @BeforeEach
  void setUp() {
    User testUser = UserFactory.createBasicUser();
    authenticatedTestUser = AuthenticatedUserFactory.createAuthenticatedUserDto(testUser);
    User adminUser = UserFactory.createAdminUser();
    authenticatedAdminUser = AuthenticatedUserFactory.createAuthenticatedUserDto(adminUser);
  }

  @Test
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(TestDataSamples.LEANNE_UUID, authenticatedAdminUser);
    User result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result.getName()).isEqualTo(TestDataSamples.LEANNE_NAME);
    assertThat(result.getEmail()).isEqualTo(TestDataSamples.LEANNE_EMAIL);
    assertThat(result.getUsername()).isEqualTo(TestDataSamples.LEANNE_USERNAME);
    assertThat(result.getUuid()).isEqualTo(TestDataSamples.LEANNE_UUID);
    assertThat(result.getId()).isEqualTo(TestDataSamples.LEANNE_ID);
  }

  @Test
  void findByUuid_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(UUID.randomUUID(), authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByUuid_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // When
    FindUserByUuidDto findUserByUuidDto1 = new FindUserByUuidDto(TestDataSamples.ERVIN_UUID, authenticatedAdminUser);
    FindUserByUuidDto findUserByUuidDto2 = new FindUserByUuidDto(TestDataSamples.CLEMENTINE_UUID, authenticatedAdminUser);
    User firstResult = findUserByUuid.findByUuid(findUserByUuidDto1);
    User secondResult = findUserByUuid.findByUuid(findUserByUuidDto2);

    // Then
    assertThat(firstResult.getName()).isEqualTo(TestDataSamples.ERVIN_NAME);
    assertThat(firstResult.getId()).isEqualTo(TestDataSamples.ERVIN_ID);

    assertThat(secondResult.getName()).isEqualTo(TestDataSamples.CLEMENTINE_NAME);
    assertThat(secondResult.getId()).isEqualTo(TestDataSamples.CLEMENTINE_ID);
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
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(TestDataSamples.PATRICIA_UUID, authenticatedAdminUser);
    User result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result.getName()).isEqualTo("Updated Patricia User");
    assertThat(result.getEmail()).isEqualTo("updated.patricia@example.com");
    assertThat(result.getUsername()).isEqualTo(TestDataSamples.PATRICIA_USERNAME);
    assertThat(result.getUuid()).isEqualTo(TestDataSamples.PATRICIA_UUID);
  }

  @Test
  void findByUuid_WhenNonAdminUserTriesToAccessOtherUserData_ShouldThrowForbidden() {
    // Given
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(TestDataSamples.LEANNE_UUID, authenticatedTestUser);

    // When & Then
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Access denied: only admins or the user themselves can view user data");
  }
}
