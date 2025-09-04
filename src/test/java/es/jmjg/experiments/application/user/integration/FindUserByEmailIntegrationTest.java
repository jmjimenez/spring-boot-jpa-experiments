package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.jmjg.experiments.application.user.exception.UserNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.dto.FindUserByEmailDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;

class FindUserByEmailIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserByEmail findUserByEmail;

  @Autowired
  private UserRepositoryImpl userRepository;

  private AuthenticatedUserDto authenticatedAdminUser;

  @BeforeEach
  void setUp() {
    User adminUser = UserFactory.createAdminUser();
    authenticatedAdminUser = AuthenticatedUserFactory.createAuthenticatedUserDto(adminUser);
  }

  @Test
  void findByEmail_WhenUserExists_ShouldReturnUser() {
    // Given
    // Leanne Graham is already in the database from Flyway migration
    String existingUserEmail = TestDataSamples.LEANNE_EMAIL;

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(existingUserEmail, authenticatedAdminUser);
    User result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result.getName()).isEqualTo(TestDataSamples.LEANNE_NAME);
    assertThat(result.getEmail()).isEqualTo(TestDataSamples.LEANNE_EMAIL);
    assertThat(result.getUsername()).isEqualTo(TestDataSamples.LEANNE_USERNAME);
    assertThat(result.getUuid()).isEqualTo(TestDataSamples.LEANNE_UUID);
  }

  @Test
  void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto("nonexistent@example.com", authenticatedAdminUser);
    assertThatThrownBy(() ->  findUserByEmail.findByEmail(findUserByEmailDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByEmail_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given
    // Use an existing user from the database (Ervin Howell)
    User existingUser = userRepository.findByUuid(TestDataSamples.ERVIN_UUID)
        .orElseThrow(() -> new RuntimeException("Test user not found in database"));

    // Update the user
    existingUser.setName("Updated Ervin User");
    existingUser.setEmail("updated.ervin@example.com");
    User updatedUser = userRepository.save(existingUser);

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto("updated.ervin@example.com", authenticatedAdminUser);
    User result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result.getName()).isEqualTo("Updated Ervin User");
    assertThat(result.getEmail()).isEqualTo("updated.ervin@example.com");
    assertThat(result.getUsername()).isEqualTo(TestDataSamples.ERVIN_USERNAME);
    assertThat(result.getUuid()).isEqualTo(TestDataSamples.ERVIN_UUID);
    assertThat(result.getId()).isEqualTo(updatedUser.getId());
  }

  @Test
  void findByEmail_WhenEmailIsEmpty_ShouldReturnEmpty() {
    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto("", authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByEmail.findByEmail(findUserByEmailDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByEmail_WhenEmailIsBlank_ShouldReturnEmpty() {
    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto("   ", authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByEmail.findByEmail(findUserByEmailDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByEmail_WhenUserIsDeleted_ShouldReturnEmpty() {
    // Given
    // Use an existing user from the database (Clementine Bauch)
    User existingUser = userRepository.findByUuid(TestDataSamples.CLEMENTINE_UUID)
        .orElseThrow(() -> new RuntimeException("Test user not found in database"));
    String userEmail = existingUser.getEmail();

    // Verify user exists before deletion
    FindUserByEmailDto findUserByEmailDto1 = new FindUserByEmailDto(userEmail, authenticatedAdminUser);
    findUserByEmail.findByEmail(findUserByEmailDto1);

    // Delete the user
    userRepository.deleteById(existingUser.getId());

    // When
    FindUserByEmailDto findUserByEmailDto2 = new FindUserByEmailDto(userEmail, authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByEmail.findByEmail(findUserByEmailDto2)).isInstanceOf(UserNotFound.class);

    // Then
  }

  @Test
  void findByEmail_WhenEmailHasDifferentCase_ShouldReturnEmpty() {
    // Given
    // Use an existing user from the database (Patricia Lebsack)
    User existingUser = userRepository.findByUuid(TestDataSamples.PATRICIA_UUID)
        .orElseThrow(() -> new RuntimeException("Test user not found in database"));

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(existingUser.getEmail().toUpperCase(),
        authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByEmail.findByEmail(findUserByEmailDto)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void findByEmail_WhenEmailHasSpecialCharacters_ShouldReturnUser() {
    // Given
    User specialUser = UserFactory.createUser("Special User", "test+tag@example.com", "specialuser");
    User savedUser = userRepository.save(specialUser);

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(savedUser.getEmail(), authenticatedAdminUser);
    User result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result.getName()).isEqualTo(specialUser.getName());
    assertThat(result.getEmail()).isEqualTo(specialUser.getEmail());
    assertThat(result.getUsername()).isEqualTo(specialUser.getUsername());
    assertThat(result.getUuid()).isEqualTo(specialUser.getUuid());
    assertThat(result.getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByEmail_WhenEmailHasSubdomain_ShouldReturnUser() {
    // Given
    User subdomainUser = UserFactory.createUser("Subdomain User", "user@subdomain.example.com", "subdomainuser");
    User savedUser = userRepository.save(subdomainUser);

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(savedUser.getEmail(), authenticatedAdminUser);
    User result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result.getName()).isEqualTo(subdomainUser.getName());
    assertThat(result.getEmail()).isEqualTo(subdomainUser.getEmail());
    assertThat(result.getUsername()).isEqualTo(subdomainUser.getUsername());
    assertThat(result.getUuid()).isEqualTo(subdomainUser.getUuid());
    assertThat(result.getId()).isEqualTo(savedUser.getId());
  }
}
