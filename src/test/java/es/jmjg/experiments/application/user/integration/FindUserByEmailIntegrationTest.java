package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.UserFactory;

class FindUserByEmailIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserByEmail findUserByEmail;

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByEmail_WhenUserExists_ShouldReturnUser() {
    // Given
    User testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    User savedUser = userRepository.save(testUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail(savedUser.getEmail());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(testUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(testUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(testUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(testUser.getUuid());
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByEmail.findByEmail("nonexistent@example.com");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailIsNull_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByEmail.findByEmail(null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given
    User secondUser = UserFactory.createUser("Second User", "second@example.com", "seconduser");
    User savedSecondUser = userRepository.save(secondUser);
    User thirdUser = UserFactory.createUser("Third User", "third@example.com", "thirduser");
    User savedThirdUser = userRepository.save(thirdUser);

    // When
    Optional<User> secondResult = findUserByEmail.findByEmail(secondUser.getEmail());
    Optional<User> thirdResult = findUserByEmail.findByEmail(thirdUser.getEmail());

    // Then
    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo(secondUser.getName());
    assertThat(secondResult.get().getEmail()).isEqualTo(secondUser.getEmail());
    assertThat(secondResult.get().getId()).isEqualTo(savedSecondUser.getId());

    assertThat(thirdResult).isPresent();
    assertThat(thirdResult.get().getName()).isEqualTo(thirdUser.getName());
    assertThat(thirdResult.get().getEmail()).isEqualTo(thirdUser.getEmail());
    assertThat(thirdResult.get().getId()).isEqualTo(savedThirdUser.getId());
  }

  @Test
  void findByEmail_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given
    User fourthUser = UserFactory.createUser("Fourth User", "fourth@example.com", "fourthuser");
    User savedUser = userRepository.save(fourthUser);
    savedUser.setName("Updated Test User");
    savedUser.setEmail("updated@example.com");
    userRepository.save(savedUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail("updated@example.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Updated Test User");
    assertThat(result.get().getEmail()).isEqualTo("updated@example.com");
    assertThat(result.get().getUsername()).isEqualTo(fourthUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(savedUser.getUuid());
  }

  @Test
  void findByEmail_WhenEmailIsEmpty_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByEmail.findByEmail("");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailIsBlank_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserByEmail.findByEmail("   ");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenUserIsDeleted_ShouldReturnEmpty() {
    // Given
    User fifthUser = UserFactory.createUser("Fifth User", "fifth@example.com", "fifthuser");
    User savedUser = userRepository.save(fifthUser);
    String userEmail = savedUser.getEmail();

    // When
    Optional<User> resultBeforeDelete = findUserByEmail.findByEmail(userEmail);
    userRepository.deleteById(savedUser.getId());
    Optional<User> resultAfterDelete = findUserByEmail.findByEmail(userEmail);

    // Then
    assertThat(resultBeforeDelete).isPresent();
    assertThat(resultAfterDelete).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailHasDifferentCase_ShouldReturnEmpty() {
    // Given
    User sixthUser = UserFactory.createUser("Sixth User", "sixth@example.com", "sixthuser");
    userRepository.save(sixthUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail(sixthUser.getEmail().toUpperCase());

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailHasSpecialCharacters_ShouldReturnUser() {
    // Given
    User specialUser = UserFactory.createUser("Special User", "test+tag@example.com", "specialuser");
    User savedUser = userRepository.save(specialUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail("test+tag@example.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Special User");
    assertThat(result.get().getEmail()).isEqualTo("test+tag@example.com");
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByEmail_WhenEmailHasSubdomain_ShouldReturnUser() {
    // Given
    User subdomainUser = UserFactory.createUser("Subdomain User", "user@subdomain.example.com", "subdomainuser");
    User savedUser = userRepository.save(subdomainUser);

    // When
    Optional<User> result = findUserByEmail.findByEmail("user@subdomain.example.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Subdomain User");
    assertThat(result.get().getEmail()).isEqualTo("user@subdomain.example.com");
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }
}
