package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

  private JwtUserDetails testUserDetails;
  private JwtUserDetails adminUserDetails;

  @BeforeEach
  void setUp() {
    var testUser = UserFactory.createBasicUser();
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);

    var adminUser = UserFactory.createAdminUser();
    adminUserDetails = UserDetailsFactory.createJwtUserDetails(adminUser);
  }

  @Test
  void deleteByUuid_WhenUserExists_ShouldDeleteUser() {
    // Given
    UUID userToDeleteUuid = TestDataSamples.LEANNE_UUID;
    assertThat(userRepository.findByUuid(userToDeleteUuid)).isPresent();

    // When
    DeleteUserDto deleteUserDto = new DeleteUserDto(userToDeleteUuid, adminUserDetails);
    deleteUser.delete(deleteUserDto);

    // Then
    Optional<User> deletedUser = userRepository.findByUuid(userToDeleteUuid);
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
  void deleteByUuid_WhenUserIsDeleted_ShouldNotAffectOtherOperations() {
    // Given
    UUID userToDeleteUuid = TestDataSamples.ERVIN_UUID;
    assertThat(userRepository.findByUuid(userToDeleteUuid)).isPresent();

    // When
    DeleteUserDto deleteUserDto = new DeleteUserDto(userToDeleteUuid, adminUserDetails);
    deleteUser.delete(deleteUserDto);

    // Then
    assertThat(userRepository.findByUuid(userToDeleteUuid)).isEmpty();

    // Verify that other operations still work
    User newUser = UserFactory.createBasicUser();
    User savedNewUser = userRepository.save(newUser);
    assertThat(savedNewUser.getId()).isNotNull();
    assertThat(userRepository.findByUuid(newUser.getUuid())).isPresent();
  }

  @Test
  void deleteByUuid_WhenUserIsNotAuthorized_ShouldThrowForbidden() {
    // Given
    UUID userToDeleteUuid = TestDataSamples.CLEMENTINE_UUID;
    assertThat(userRepository.findByUuid(userToDeleteUuid)).isPresent();

    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(userToDeleteUuid, testUserDetails);
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Only admin users can delete users");

    // Verify user still exists (not deleted)
    assertThat(userRepository.findByUuid(userToDeleteUuid)).isPresent();
  }

}
