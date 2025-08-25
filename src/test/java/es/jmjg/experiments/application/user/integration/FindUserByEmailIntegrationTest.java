package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.FindUserByEmailDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.UserFactory;

class FindUserByEmailIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserByEmail findUserByEmail;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void findByEmail_WhenUserExists_ShouldReturnUser() {
    // Given
    User testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    User savedUser = userRepository.save(testUser);

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(savedUser.getEmail());
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

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
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto("nonexistent@example.com");
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailIsNull_ShouldReturnEmpty() {
    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(null);
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

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
    FindUserByEmailDto findUserByEmailDto1 = new FindUserByEmailDto(secondUser.getEmail());
    FindUserByEmailDto findUserByEmailDto2 = new FindUserByEmailDto(thirdUser.getEmail());
    Optional<User> secondResult = findUserByEmail.findByEmail(findUserByEmailDto1);
    Optional<User> thirdResult = findUserByEmail.findByEmail(findUserByEmailDto2);

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
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto("updated@example.com");
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Updated Test User");
    assertThat(result.get().getEmail()).isEqualTo("updated@example.com");
    assertThat(result.get().getUsername()).isEqualTo(fourthUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(fourthUser.getUuid());
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByEmail_WhenEmailIsEmpty_ShouldReturnEmpty() {
    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto("");
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailIsBlank_ShouldReturnEmpty() {
    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto("   ");
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenUserIsDeleted_ShouldReturnEmpty() {
    // Given
    User fifthUser = UserFactory.createUser("Fifth User", "fifth@example.com", "fifthuser");
    User savedUser = userRepository.save(fifthUser);
    String userEmail = savedUser.getEmail();

    // Verify user exists before deletion
    FindUserByEmailDto findUserByEmailDto1 = new FindUserByEmailDto(userEmail);
    Optional<User> resultBeforeDelete = findUserByEmail.findByEmail(findUserByEmailDto1);
    assertThat(resultBeforeDelete).isPresent();

    // Delete the user
    userRepository.deleteById(savedUser.getId());

    // When
    FindUserByEmailDto findUserByEmailDto2 = new FindUserByEmailDto(userEmail);
    Optional<User> resultAfterDelete = findUserByEmail.findByEmail(findUserByEmailDto2);

    // Then
    assertThat(resultAfterDelete).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailHasDifferentCase_ShouldReturnEmpty() {
    // Given
    User sixthUser = UserFactory.createUser("Sixth User", "sixth@example.com", "sixthuser");
    userRepository.save(sixthUser);

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(sixthUser.getEmail().toUpperCase());
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByEmail_WhenEmailHasSpecialCharacters_ShouldReturnUser() {
    // Given
    User specialUser = UserFactory.createUser("Special User", "test+tag@example.com", "specialuser");
    User savedUser = userRepository.save(specialUser);

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(savedUser.getEmail());
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(specialUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(specialUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(specialUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(specialUser.getUuid());
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByEmail_WhenEmailHasSubdomain_ShouldReturnUser() {
    // Given
    User subdomainUser = UserFactory.createUser("Subdomain User", "user@subdomain.example.com", "subdomainuser");
    User savedUser = userRepository.save(subdomainUser);

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(savedUser.getEmail());
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(subdomainUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(subdomainUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(subdomainUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(subdomainUser.getUuid());
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }
}
