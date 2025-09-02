package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.dto.FindUserByUsernameDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class FindUserByUsernameIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserByUsername findUserByUsername;

  @Autowired
  private UserRepositoryImpl userRepository;

  private AuthenticatedUserDto authenticatedTestUser;
  private AuthenticatedUserDto authenticatedAdminUser;

  @BeforeEach
  void setUp() {
    User testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    authenticatedTestUser = UserDetailsFactory.createAuthenticatedUserDto(testUser);
    User adminUser = UserFactory.createUser("Admin User", "admin@example.com", "admin");
    authenticatedAdminUser = UserDetailsFactory.createAuthenticatedUserDto(adminUser);
  }

  @Test
  void findByUsername_WhenUserExists_ShouldReturnUser() {
    // Given - using existing test data from migration
    String testUsername = TestDataSamples.ERVIN_USERNAME;

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUsername, authenticatedAdminUser);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(TestDataSamples.ERVIN_NAME);
    assertThat(result.get().getEmail()).isEqualTo(TestDataSamples.ERVIN_EMAIL);
    assertThat(result.get().getUsername()).isEqualTo(TestDataSamples.ERVIN_USERNAME);
    assertThat(result.get().getUuid()).isEqualTo(TestDataSamples.ERVIN_UUID);
    assertThat(result.get().getId()).isEqualTo(2); // Second user from migration
  }

  @Test
  void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("nonexistentuser", authenticatedAdminUser);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given - using existing test data from migration
    String secondUsername = TestDataSamples.ERVIN_USERNAME;
    String thirdUsername = TestDataSamples.CLEMENTINE_USERNAME;

    // When
    FindUserByUsernameDto findUserByUsernameDto1 = new FindUserByUsernameDto(secondUsername, authenticatedAdminUser);
    FindUserByUsernameDto findUserByUsernameDto2 = new FindUserByUsernameDto(thirdUsername, authenticatedAdminUser);
    Optional<User> secondResult = findUserByUsername.findByUsername(findUserByUsernameDto1);
    Optional<User> thirdResult = findUserByUsername.findByUsername(findUserByUsernameDto2);

    // Then
    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo(TestDataSamples.ERVIN_NAME);
    assertThat(secondResult.get().getId()).isEqualTo(2); // Second user from migration

    assertThat(thirdResult).isPresent();
    assertThat(thirdResult.get().getName()).isEqualTo(TestDataSamples.CLEMENTINE_NAME);
    assertThat(thirdResult.get().getId()).isEqualTo(3); // Third user from migration
  }

  @Test
  void findByUsername_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given - using existing test data from migration
    String testUsername = TestDataSamples.LEANNE_USERNAME;
    FindUserByUsernameDto findUserByUsernameDto1 = new FindUserByUsernameDto(testUsername, authenticatedAdminUser);
    User existingUser = findUserByUsername.findByUsername(findUserByUsernameDto1).orElseThrow();

    // Update the user
    existingUser.setName("Updated Test User");
    existingUser.setEmail("updated@example.com");
    userRepository.save(existingUser);

    // When
    FindUserByUsernameDto findUserByUsernameDto2 = new FindUserByUsernameDto(testUsername, authenticatedAdminUser);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto2);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Updated Test User");
    assertThat(result.get().getEmail()).isEqualTo("updated@example.com");
    assertThat(result.get().getUsername()).isEqualTo(TestDataSamples.LEANNE_USERNAME);
    assertThat(result.get().getUuid()).isEqualTo(TestDataSamples.LEANNE_UUID);
  }

  @Test
  void findByUsername_WhenUsernameIsEmpty_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("", authenticatedAdminUser);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameIsBlank_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("   ", authenticatedAdminUser);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameHasDifferentCase_ShouldReturnEmpty() {
    // Given - using existing test data from migration
    String testUsername = TestDataSamples.LEANNE_USERNAME.toUpperCase();

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUsername, authenticatedAdminUser);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameHasLeadingAndTrailingSpaces_ShouldReturnEmpty() {
    // Given - using existing test data from migration
    String testUsername = TestDataSamples.LEANNE_USERNAME;

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(" " + testUsername + " ", authenticatedAdminUser);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenAuthenticatedUserRequestsOwnData_ShouldReturnUser() {
    // Given - testUser requesting their own data
    User leanneUser = userRepository.findByUsername(TestDataSamples.LEANNE_USERNAME).orElseThrow();
    AuthenticatedUserDto authenticatedUserLeanne = UserDetailsFactory.createAuthenticatedUserDto(leanneUser);
    String leanneUsername = authenticatedUserLeanne.username();

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(leanneUsername, authenticatedUserLeanne);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getUsername()).isEqualTo(leanneUsername);
  }

  @Test
  void findByUsername_WhenAuthenticatedUserRequestsOtherUserData_ShouldThrowForbidden() {
    // Given - testUser requesting another user's data
    String otherUsername = TestDataSamples.LEANNE_USERNAME;

    // When & Then
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(otherUsername, authenticatedTestUser);
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto))
        .isInstanceOf(es.jmjg.experiments.application.shared.exception.Forbidden.class)
        .hasMessage("Access denied: only admins or the user themselves can view user data");
  }
}
