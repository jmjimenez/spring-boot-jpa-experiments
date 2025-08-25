package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.FindUserByUsernameDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;

class FindUserByUsernameIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserByUsername findUserByUsername;

  @Autowired
  private UserRepositoryImpl userRepository;

  // Test data from migration
  private static final UUID LEANNE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
  private static final String LEANNE_USERNAME = "leanne_graham";

  private static final UUID ERVIN_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
  private static final String ERVIN_USERNAME = "ervin_howell";
  private static final String ERVIN_NAME = "Ervin Howell";
  private static final String ERVIN_EMAIL = "ervin.howell@example.com";

  private static final String CLEMENTINE_USERNAME = "clementine_bauch";
  private static final String CLEMENTINE_NAME = "Clementine Bauch";

  @Test
  void findByUsername_WhenUserExists_ShouldReturnUser() {
    // Given - using existing test data from migration
    String testUsername = ERVIN_USERNAME;

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUsername);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(ERVIN_NAME);
    assertThat(result.get().getEmail()).isEqualTo(ERVIN_EMAIL);
    assertThat(result.get().getUsername()).isEqualTo(ERVIN_USERNAME);
    assertThat(result.get().getUuid()).isEqualTo(ERVIN_UUID);
    assertThat(result.get().getId()).isEqualTo(2); // Second user from migration
  }

  @Test
  void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("nonexistentuser");
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameIsNull_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(null);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given - using existing test data from migration
    String secondUsername = ERVIN_USERNAME;
    String thirdUsername = CLEMENTINE_USERNAME;

    // When
    FindUserByUsernameDto findUserByUsernameDto1 = new FindUserByUsernameDto(secondUsername);
    FindUserByUsernameDto findUserByUsernameDto2 = new FindUserByUsernameDto(thirdUsername);
    Optional<User> secondResult = findUserByUsername.findByUsername(findUserByUsernameDto1);
    Optional<User> thirdResult = findUserByUsername.findByUsername(findUserByUsernameDto2);

    // Then
    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo(ERVIN_NAME);
    assertThat(secondResult.get().getId()).isEqualTo(2); // Second user from migration

    assertThat(thirdResult).isPresent();
    assertThat(thirdResult.get().getName()).isEqualTo(CLEMENTINE_NAME);
    assertThat(thirdResult.get().getId()).isEqualTo(3); // Third user from migration
  }

  @Test
  void findByUsername_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given - using existing test data from migration
    String testUsername = LEANNE_USERNAME;
    FindUserByUsernameDto findUserByUsernameDto1 = new FindUserByUsernameDto(testUsername);
    User existingUser = findUserByUsername.findByUsername(findUserByUsernameDto1).orElseThrow();

    // Update the user
    existingUser.setName("Updated Test User");
    existingUser.setEmail("updated@example.com");
    userRepository.save(existingUser);

    // When
    FindUserByUsernameDto findUserByUsernameDto2 = new FindUserByUsernameDto(testUsername);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto2);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Updated Test User");
    assertThat(result.get().getEmail()).isEqualTo("updated@example.com");
    assertThat(result.get().getUsername()).isEqualTo(LEANNE_USERNAME);
    assertThat(result.get().getUuid()).isEqualTo(LEANNE_UUID);
  }

  @Test
  void findByUsername_WhenUsernameIsEmpty_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("");
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameIsBlank_ShouldReturnEmpty() {
    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("   ");
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameHasDifferentCase_ShouldReturnEmpty() {
    // Given - using existing test data from migration
    String testUsername = LEANNE_USERNAME.toUpperCase();

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUsername);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUsername_WhenUsernameHasLeadingAndTrailingSpaces_ShouldReturnEmpty() {
    // Given - using existing test data from migration
    String testUsername = LEANNE_USERNAME;

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(" " + testUsername + " ");
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
  }
}
