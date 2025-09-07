package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.user.dto.UpdateUserDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class UpdateUserTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UpdateUser updateUser;

  private User existingUser;
  private User testUser;
  private AuthenticatedUserDto authenticatedTestUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    authenticatedTestUser = AuthenticatedUserFactory.createAuthenticatedUserDto(testUser);
    existingUser = UserFactory.createUser(1, testUser.getUuid(), "Old Name", "old@example.com", "olduser");
  }

  @Test
  void update_WhenUserExistsAndIsAuthenticatedUser_ShouldUpdateFields() {
    // Given
    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    var updateUserDto = createUpdateUserDto(testUser.getUuid(), authenticatedTestUser, "New Name", "new@example.com");
    User result = updateUser.update(updateUserDto);

    // Then
    assertThat(result.getName()).isEqualTo("New Name");
    assertThat(result.getEmail()).isEqualTo("new@example.com");
    assertThat(result.getUuid()).isEqualTo(testUser.getUuid());
    verify(userRepository, times(1)).save(existingUser);
  }

  @Test
  void update_WhenUserExistsAndAuthenticatedUserIsAdmin_ShouldUpdateFields() {
    // Given
    var adminUser = UserFactory.createAdminUser();
    var authenticatedAdminUser = AuthenticatedUserFactory.createAuthenticatedUserDto(adminUser);
    var updateUserDto = createUpdateUserDto(testUser.getUuid(), authenticatedAdminUser, "New Name", "new@example.com");

    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = updateUser.update(updateUserDto);

    // Then
    assertThat(result.getName()).isEqualTo("New Name");
    assertThat(result.getEmail()).isEqualTo("new@example.com");
    assertThat(result.getUuid()).isEqualTo(testUser.getUuid());
    verify(userRepository, times(1)).save(existingUser);
  }

  @Test
  void update_WhenUserExistsAndIsNotAuthenticatedUser_ShouldNotUpdateFields() {
    // Given
    var otherUser = UserFactory.createBasicUser();
    var authenticatedOtherUser = AuthenticatedUserFactory.createAuthenticatedUserDto(otherUser);
    var updateUserDto = createUpdateUserDto(testUser.getUuid(), authenticatedOtherUser, "New Name", "new@example.com");

    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(existingUser));

    // When & Then
    assertThatThrownBy(() -> updateUser.update(updateUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Access denied: only admins or the user themselves can update user data");
    verify(userRepository, never()).save(any());
  }

  @Test
  void update_WhenUserDoesNotExist_ShouldThrow() {
    // Given
    var newId = UUID.randomUUID();
    var updateUserDto = createUpdateUserDto(newId, authenticatedTestUser, "New Name", "new@example.com");
    when(userRepository.findByUuid(newId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> updateUser.update(updateUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found with uuid: " + newId);
    verify(userRepository, never()).save(any());
  }

  private UpdateUserDto createUpdateUserDto(UUID uuid, AuthenticatedUserDto authenticatedUser, String name,
      String email) {
    return new UpdateUserDto(
        uuid,
        name,
        email,
        authenticatedUser);
  }
}
