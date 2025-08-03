package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.FindUserById;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.UserFactory;

class FindUserByIdIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserById findUserById;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void findById_WhenUserExists_ShouldReturnUser() {
    // Given
    User testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    User savedUser = userRepository.save(testUser);

    // When
    Optional<User> result = findUserById.findById(savedUser.getId());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(testUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(testUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(testUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(testUser.getUuid());
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findById_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserById.findById(999);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findById_WhenIdIsNull_ShouldThrowException() {
    // When & Then
    assertThatThrownBy(() -> findUserById.findById(null))
        .isInstanceOf(org.springframework.dao.InvalidDataAccessApiUsageException.class)
        .hasMessageContaining("The given id must not be null");
  }

  @Test
  void findById_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given
    User firstUser = UserFactory.createUser("First User", "first@example.com", "firstuser");
    User secondUser = UserFactory.createUser("Second User", "second@example.com", "seconduser");

    User savedFirstUser = userRepository.save(firstUser);
    User savedSecondUser = userRepository.save(secondUser);

    // When
    Optional<User> firstResult = findUserById.findById(savedFirstUser.getId());
    Optional<User> secondResult = findUserById.findById(savedSecondUser.getId());

    // Then
    assertThat(firstResult).isPresent();
    assertThat(firstResult.get().getName()).isEqualTo(firstUser.getName());
    assertThat(firstResult.get().getId()).isEqualTo(savedFirstUser.getId());

    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo(secondUser.getName());
    assertThat(secondResult.get().getId()).isEqualTo(savedSecondUser.getId());
  }

  @Test
  void findById_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given
    User thirdUser = UserFactory.createUser("Third User", "third@example.com", "thirduser");
    User savedThirdUser = userRepository.save(thirdUser);

    savedThirdUser.setName("Updated Third User");
    savedThirdUser.setEmail("updated@example.com");
    userRepository.save(savedThirdUser);

    // When
    Optional<User> result = findUserById.findById(savedThirdUser.getId());

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(thirdUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(thirdUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(thirdUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(thirdUser.getUuid());
  }

  @Test
  void findById_WhenIdIsZero_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserById.findById(0);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findById_WhenIdIsNegative_ShouldReturnEmpty() {
    // When
    Optional<User> result = findUserById.findById(-1);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findById_WhenUserIsDeleted_ShouldReturnEmpty() {
    // Given
    User fourthUser = UserFactory.createUser("Fourth User", "fourth@example.com", "fourthuser");
    User savedUser = userRepository.save(fourthUser);
    Integer userId = savedUser.getId();

    // When
    Optional<User> resultBeforeDelete = findUserById.findById(userId);
    userRepository.deleteById(userId);
    Optional<User> resultAfterDelete = findUserById.findById(userId);

    // Then
    assertThat(resultBeforeDelete).isPresent();
    assertThat(resultAfterDelete).isEmpty();
  }

  @Test
  void findById_WhenMultipleUsersWithSameName_ShouldReturnCorrectUser() {
    // Given
    User fifthUser = UserFactory.createUser("Fifth User", "fifth@example.com", "fifthuser");
    User sixthUser = UserFactory.createUser("Sixth User", "sixth@example.com", "sixthuser");

    User savedFifthUser = userRepository.save(fifthUser);
    User savedSixthUser = userRepository.save(sixthUser);

    // When
    Optional<User> firstResult = findUserById.findById(savedFifthUser.getId());
    Optional<User> secondResult = findUserById.findById(savedSixthUser.getId());

    // Then
    assertThat(firstResult).isPresent();
    assertThat(firstResult.get().getName()).isEqualTo(fifthUser.getName());
    assertThat(firstResult.get().getEmail()).isEqualTo(fifthUser.getEmail());
    assertThat(firstResult.get().getId()).isEqualTo(savedFifthUser.getId());

    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo(sixthUser.getName());
    assertThat(secondResult.get().getEmail()).isEqualTo(sixthUser.getEmail());
    assertThat(secondResult.get().getId()).isEqualTo(savedSixthUser.getId());
  }
}
