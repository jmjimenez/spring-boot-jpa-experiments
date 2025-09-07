package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.dto.FindUserByUsernameDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
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
    authenticatedTestUser = AuthenticatedUserFactory.createAuthenticatedUserDto(testUser);
    User adminUser = UserFactory.createUser("Admin User", "admin@example.com", "admin");
    authenticatedAdminUser = AuthenticatedUserFactory.createAuthenticatedUserDto(adminUser);
  }

  @Test
  void findByUsername_WhenUserExists_ShouldReturnUser() {
    // Given - using existing test data from migration
    String testUsername = TestDataSamples.ERVIN_USERNAME;

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUsername, authenticatedAdminUser);
    User result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result.getName()).isEqualTo(TestDataSamples.ERVIN_NAME);
    assertThat(result.getEmail()).isEqualTo(TestDataSamples.ERVIN_EMAIL);
    assertThat(result.getUsername()).isEqualTo(TestDataSamples.ERVIN_USERNAME);
    assertThat(result.getUuid()).isEqualTo(TestDataSamples.ERVIN_UUID);
    assertThat(result.getId()).isEqualTo(2); // Second user from migration
  }

  @Test
  void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("nonexistentuser", authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByUsername_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given - using existing test data from migration
    String secondUsername = TestDataSamples.ERVIN_USERNAME;
    String thirdUsername = TestDataSamples.CLEMENTINE_USERNAME;

    // When
    FindUserByUsernameDto findUserByUsernameDto1 = new FindUserByUsernameDto(secondUsername, authenticatedAdminUser);
    FindUserByUsernameDto findUserByUsernameDto2 = new FindUserByUsernameDto(thirdUsername, authenticatedAdminUser);
    User secondResult = findUserByUsername.findByUsername(findUserByUsernameDto1);
    User thirdResult = findUserByUsername.findByUsername(findUserByUsernameDto2);

    // Then
    assertThat(secondResult.getName()).isEqualTo(TestDataSamples.ERVIN_NAME);
    assertThat(secondResult.getId()).isEqualTo(2); // Second user from migration

    assertThat(thirdResult.getName()).isEqualTo(TestDataSamples.CLEMENTINE_NAME);
    assertThat(thirdResult.getId()).isEqualTo(3); // Third user from migration
  }

  @Test
  void findByUsername_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given - using existing test data from migration
    String testUsername = TestDataSamples.LEANNE_USERNAME;
    FindUserByUsernameDto findUserByUsernameDto1 = new FindUserByUsernameDto(testUsername, authenticatedAdminUser);
    User existingUser = findUserByUsername.findByUsername(findUserByUsernameDto1);

    // Update the user
    existingUser.setName("Updated Test User");
    existingUser.setEmail("updated@example.com");
    userRepository.save(existingUser);

    // When
    FindUserByUsernameDto findUserByUsernameDto2 = new FindUserByUsernameDto(testUsername, authenticatedAdminUser);
    User result = findUserByUsername.findByUsername(findUserByUsernameDto2);

    // Then
    assertThat(result.getName()).isEqualTo("Updated Test User");
    assertThat(result.getEmail()).isEqualTo("updated@example.com");
    assertThat(result.getUsername()).isEqualTo(TestDataSamples.LEANNE_USERNAME);
    assertThat(result.getUuid()).isEqualTo(TestDataSamples.LEANNE_UUID);
  }

  @Test
  void findByUsername_WhenUsernameIsEmpty_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("", authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByUsername_WhenUsernameIsBlank_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("   ", authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByUsername_WhenUsernameHasDifferentCase_ShouldReturnEmpty() {
    // Given - using existing test data from migration
    String testUsername = TestDataSamples.LEANNE_USERNAME.toUpperCase();

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUsername, authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByUsername_WhenUsernameHasLeadingAndTrailingSpaces_ShouldReturnEmpty() {
    // Given - using existing test data from migration
    String testUsername = TestDataSamples.LEANNE_USERNAME;

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(" " + testUsername + " ", authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByUsername_WhenAuthenticatedUserRequestsOwnData_ShouldReturnUser() {
    // Given - testUser requesting their own data
    User leanneUser = userRepository.findByUsername(TestDataSamples.LEANNE_USERNAME).orElseThrow();
    AuthenticatedUserDto authenticatedUserLeanne = AuthenticatedUserFactory.createAuthenticatedUserDto(leanneUser);
    String leanneUsername = authenticatedUserLeanne.username();

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(leanneUsername, authenticatedUserLeanne);
    User result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result.getUsername()).isEqualTo(leanneUsername);
  }

  @Test
  void findByUsername_WhenAuthenticatedUserRequestsOtherUserData_ShouldThrowForbidden() {
    // Given - testUser requesting another user's data
    String otherUsername = TestDataSamples.LEANNE_USERNAME;

    // When & Then
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(otherUsername, authenticatedTestUser);
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Access denied: only admins or the user themselves can view user data");
  }
}
