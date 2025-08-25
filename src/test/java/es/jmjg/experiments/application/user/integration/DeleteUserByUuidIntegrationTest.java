package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.DeleteUser;
import es.jmjg.experiments.application.user.DeleteUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.UserFactory;

class DeleteUserByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private DeleteUser deleteUser;

  @Autowired
  private UserRepositoryImpl userRepository;

  private User testUser;
  private UUID testUuid;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
  }

  @Test
  void deleteByUuid_WhenUserExists_ShouldDeleteUser() {
    // Given
    userRepository.save(testUser);
    assertThat(userRepository.findByUuid(testUuid)).isPresent();

    // When
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid);
    deleteUser.deleteByUuid(deleteUserDto);

    // Then
    Optional<User> deletedUser = userRepository.findByUuid(testUuid);
    assertThat(deletedUser).isEmpty();
  }

  @Test
  void deleteByUuid_WhenUserDoesNotExist_ShouldNotThrowException() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    assertThat(userRepository.findByUuid(nonExistentUuid)).isEmpty();

    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(nonExistentUuid);
    assertThatCode(() -> deleteUser.deleteByUuid(deleteUserDto))
        .doesNotThrowAnyException();
  }

  @Test
  void deleteByUuid_WhenUuidIsNull_ShouldNotThrowException() {
    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(null);
    assertThatCode(() -> deleteUser.deleteByUuid(deleteUserDto))
        .doesNotThrowAnyException();
  }

  @Test
  void deleteByUuid_WhenMultipleUsersExist_ShouldDeleteOnlyTargetUser() {
    // Given
    UUID secondUuid = UUID.randomUUID();
    User secondUser = UserFactory.createUser(secondUuid, "Second User", "second@example.com", "seconduser");

    userRepository.save(testUser);
    userRepository.save(secondUser);

    assertThat(userRepository.findByUuid(testUuid)).isPresent();
    assertThat(userRepository.findByUuid(secondUuid)).isPresent();

    // When
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid);
    deleteUser.deleteByUuid(deleteUserDto);

    // Then
    assertThat(userRepository.findByUuid(testUuid)).isEmpty();
    assertThat(userRepository.findByUuid(secondUuid)).isPresent();
    assertThat(userRepository.findByUuid(secondUuid).get().getName()).isEqualTo("Second User");
  }

  @Test
  void deleteByUuid_WhenUserIsDeleted_ShouldNotAffectOtherOperations() {
    // Given
    userRepository.save(testUser);
    assertThat(userRepository.findByUuid(testUuid)).isPresent();

    // When
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid);
    deleteUser.deleteByUuid(deleteUserDto);

    // Then
    assertThat(userRepository.findByUuid(testUuid)).isEmpty();

    // Verify that other operations still work
    User newUser = UserFactory.createUser("New User", "new@example.com", "newuser");
    User savedNewUser = userRepository.save(newUser);
    assertThat(savedNewUser.getId()).isNotNull();
    assertThat(userRepository.findByUuid(newUser.getUuid())).isPresent();
  }

  @Test
  void deleteByUuid_WhenCalledMultipleTimes_ShouldHandleGracefully() {
    // Given
    userRepository.save(testUser);
    assertThat(userRepository.findByUuid(testUuid)).isPresent();

    // When
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid);
    deleteUser.deleteByUuid(deleteUserDto);
    deleteUser.deleteByUuid(deleteUserDto); // Second call on already deleted user

    // Then
    assertThat(userRepository.findByUuid(testUuid)).isEmpty();
  }

  @Test
  void deleteByUuid_WhenDatabaseIsEmpty_ShouldNotThrowException() {
    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(UUID.randomUUID());
    assertThatCode(() -> deleteUser.deleteByUuid(deleteUserDto))
        .doesNotThrowAnyException();
  }
}
