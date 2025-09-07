package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;

class SaveUserIntegrationTest extends BaseIntegration {

  @Autowired
  private SaveUser saveUser;

  @Autowired
  private UserRepositoryImpl userRepository;

  private AuthenticatedUserDto authenticatedTestUser;
  private AuthenticatedUserDto authenticatedAdminUser;

  @BeforeEach
  void setUp() {
    User adminUser = UserFactory.createUser("Admin User", "admin@example.com", "admin");
    User testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    authenticatedAdminUser = AuthenticatedUserFactory.createAuthenticatedUserDto(adminUser);
    authenticatedTestUser = AuthenticatedUserFactory.createAuthenticatedUserDto(testUser);
  }

  @Test
  void save_WhenUserIsValid_ShouldSaveAndReturnUser() {
    User userToSave = UserFactory.createUser("User 02", "user02@example.com", "user02");
    UUID originalUuid = userToSave.getUuid();

    SaveUserDto saveUserDto = generateSaveUserDto(userToSave, authenticatedAdminUser);

    User result = saveUser.save(saveUserDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getName()).isEqualTo(userToSave.getName());
    assertThat(result.getEmail()).isEqualTo(userToSave.getEmail());
    assertThat(result.getUsername()).isEqualTo(userToSave.getUsername());
    assertThat(result.getUuid()).isEqualTo(originalUuid);

    Optional<User> dbUser = userRepository.findById(result.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getName()).isEqualTo(userToSave.getName());
    assertThat(dbUser.get().getEmail()).isEqualTo(userToSave.getEmail());
    assertThat(dbUser.get().getUsername()).isEqualTo(userToSave.getUsername());
    assertThat(dbUser.get().getUuid()).isEqualTo(originalUuid);
  }

  @Test
  void save_WhenUserIsNotAdmin_ShouldThrowForbiddenException() {
    User userToSave = UserFactory.createBasicUser();

    SaveUserDto saveUserDto = generateSaveUserDto(userToSave, authenticatedTestUser);

    assertThatThrownBy(() -> saveUser.save(saveUserDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Only administrators can create users");
  }

  @Test
  void save_WhenUserHasValidData_ShouldSaveAndReturnUser() {
    User userToSave = UserFactory.createUser("User 03", "user03@example.com", "user03");

    SaveUserDto saveUserDto = generateSaveUserDto(userToSave, authenticatedAdminUser);

    User result = saveUser.save(saveUserDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getUuid()).isNotNull();
    assertThat(result.getName()).isEqualTo(userToSave.getName());
    assertThat(result.getEmail()).isEqualTo(userToSave.getEmail());
    assertThat(result.getUsername()).isEqualTo(userToSave.getUsername());

    Optional<User> dbUser = userRepository.findById(result.getId());
    assertThat(dbUser).isPresent();
    assertThat(dbUser.get().getUuid()).isNotNull();
  }

  @Test
  void save_WhenMultipleUsers_ShouldSaveAllUsersWithDifferentIds() {
    User user1 = UserFactory.createUser("User 04", "user04@example.com", "user04");
    User user2 = UserFactory.createUser("User 05", "user05@example.com", "user05");

    SaveUserDto saveUserDto1 = generateSaveUserDto(user1, authenticatedAdminUser);
    SaveUserDto saveUserDto2 = generateSaveUserDto(user2, authenticatedAdminUser);

    User result1 = saveUser.save(saveUserDto1);
    User result2 = saveUser.save(saveUserDto2);

    assertThat(result1.getId()).isNotNull();
    assertThat(result2.getId()).isNotNull();
    assertThat(result1.getId()).isNotEqualTo(result2.getId());

    Optional<User> dbUser1 = userRepository.findById(result1.getId());
    Optional<User> dbUser2 = userRepository.findById(result2.getId());
    assertThat(dbUser1).isPresent();
    assertThat(dbUser2).isPresent();
    assertThat(dbUser1.get().getName()).isEqualTo(user1.getName());
    assertThat(dbUser2.get().getName()).isEqualTo(user2.getName());
  }

  @Test
  void save_WhenUserWithExistingUuid_ShouldThrowException() {
    // Given - Try to create a user with an existing UUID from TestDataSamples
    User userWithExistingUuid = UserFactory.createUser(
        TestDataSamples.LEANNE_UUID,
        "Duplicate User",
        "duplicate@example.com",
        "duplicate_user");

    SaveUserDto saveUserDto = generateSaveUserDto(userWithExistingUuid, authenticatedAdminUser);

    // When & Then
    assertThatThrownBy(() -> saveUser.save(saveUserDto))
        .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
  }

  @Test
  void save_WhenUserWithExistingEmail_ShouldThrowException() {
    // Given - Try to create a user with an existing email from TestDataSamples
    User userWithExistingEmail = UserFactory.createUser(
        "Duplicate User",
        TestDataSamples.LEANNE_EMAIL,
        "duplicate_user");

    SaveUserDto saveUserDto = generateSaveUserDto(userWithExistingEmail, authenticatedAdminUser);

    // When & Then
    assertThatThrownBy(() -> saveUser.save(saveUserDto))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void save_WhenUserWithExistingUsername_ShouldThrowException() {
    // Given - Try to create a user with an existing username from TestDataSamples
    User userWithExistingUsername = UserFactory.createUser(
        "Duplicate User",
        "duplicate@example.com",
        TestDataSamples.LEANNE_USERNAME);

    SaveUserDto saveUserDto = generateSaveUserDto(userWithExistingUsername, authenticatedAdminUser);

    // When & Then
    assertThatThrownBy(() -> saveUser.save(saveUserDto))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  private SaveUserDto generateSaveUserDto(User user, AuthenticatedUserDto authenticatedUser) {
    return new SaveUserDto(
        user.getUuid(),
        user.getName(),
        user.getEmail(),
        user.getUsername(),
        user.getPassword(),
        authenticatedUser);
  }
}
