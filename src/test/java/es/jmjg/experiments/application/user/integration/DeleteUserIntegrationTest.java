package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.DeleteUser;
import es.jmjg.experiments.application.user.dto.DeleteUserDto;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class DeleteUserIntegrationTest extends BaseIntegration {

  @Autowired
  private DeleteUser deleteUser;

  @Autowired
  private UserRepositoryImpl userRepository;

  private User testUser;
  private UUID testUuid;
  private JwtUserDetails testUserDetails;
  private User adminUser;
  private JwtUserDetails adminUserDetails;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);

    adminUser = UserFactory.createUser(TestDataSamples.ADMIN_UUID, TestDataSamples.ADMIN_NAME,
        TestDataSamples.ADMIN_EMAIL, TestDataSamples.ADMIN_USERNAME);
    adminUserDetails = UserDetailsFactory.createJwtUserDetails(adminUser);
  }

  @Test
  void deleteByUuid_WhenUserExists_ShouldDeleteUser() {
    // Given
    userRepository.save(testUser);
    assertThat(userRepository.findByUuid(testUuid)).isPresent();

    // When
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid, adminUserDetails);
    deleteUser.delete(deleteUserDto);

    // Then
    Optional<User> deletedUser = userRepository.findByUuid(testUuid);
    assertThat(deletedUser).isEmpty();
  }

  @Test
  void deleteByUuid_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    assertThat(userRepository.findByUuid(nonExistentUuid)).isEmpty();

    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(nonExistentUuid, adminUserDetails);
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(UserNotFound.class)
        .hasMessage("User not found with uuid: " + nonExistentUuid);
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
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid, adminUserDetails);
    deleteUser.delete(deleteUserDto);

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
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid, adminUserDetails);
    deleteUser.delete(deleteUserDto);

    // Then
    assertThat(userRepository.findByUuid(testUuid)).isEmpty();

    // Verify that other operations still work
    User newUser = UserFactory.createUser("New User", "new@example.com", "newuser");
    User savedNewUser = userRepository.save(newUser);
    assertThat(savedNewUser.getId()).isNotNull();
    assertThat(userRepository.findByUuid(newUser.getUuid())).isPresent();
  }

  @Test
  void deleteByUuid_WhenUserIsNotAuthorized_ShouldThrowForbidden() {
    // Given
    userRepository.save(testUser);
    assertThat(userRepository.findByUuid(testUuid)).isPresent();

    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid, testUserDetails);
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Only admin users can delete users");

    // Verify user still exists (not deleted)
    assertThat(userRepository.findByUuid(testUuid)).isPresent();
  }

}
