package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.user.dto.DeleteUserDto;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class DeleteUserTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DeleteUser deleteUser;

  private User testUser;
  private AuthenticatedUserDto authenticatedTestUser;
  private AuthenticatedUserDto authenticatedAdminUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    var adminUser = UserFactory.createAdminUser();
    authenticatedTestUser = AuthenticatedUserFactory.createAuthenticatedUserDto(testUser);
    authenticatedAdminUser = AuthenticatedUserFactory.createAuthenticatedUserDto(adminUser);
  }

  @Test
  void delete_WhenUserExists_ShouldDeleteUser() {
    // Given
    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    doNothing().when(userRepository).deleteByUuid(testUser.getUuid());

    // When
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUser.getUuid(), authenticatedAdminUser);
    deleteUser.delete(deleteUserDto);

    // Then
    verify(userRepository, times(1)).findByUuid(testUser.getUuid());
    verify(userRepository, times(1)).deleteByUuid(testUser.getUuid());
  }

  @Test
  void delete_WhenUserDoesNotExist_ShouldThrowUserNotFound() {
    // Given
    var nonExistentUuid = UUID.randomUUID();

    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(nonExistentUuid, authenticatedAdminUser);
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(UserNotFound.class)
        .hasMessage("User not found with uuid: " + nonExistentUuid);
    verify(userRepository, never()).deleteByUuid(nonExistentUuid);
  }

  @Test
  void delete_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    doThrow(new RuntimeException("Database error")).when(userRepository).deleteByUuid(testUser.getUuid());

    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUser.getUuid(), authenticatedAdminUser);
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).deleteByUuid(testUser.getUuid());
  }

  @Test
  void delete_WhenCalledMultipleTimes_ShouldCallRepositoryEachTime() {
    // Given
    var secondUuid = UUID.randomUUID();
    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    when(userRepository.findByUuid(secondUuid)).thenReturn(Optional.of(testUser));
    doNothing().when(userRepository).deleteByUuid(any(UUID.class));

    // When
    DeleteUserDto deleteUserDto1 = new DeleteUserDto(testUser.getUuid(), authenticatedAdminUser);
    deleteUser.delete(deleteUserDto1);
    DeleteUserDto deleteUserDto2 = new DeleteUserDto(secondUuid, authenticatedAdminUser);
    deleteUser.delete(deleteUserDto2);

    // Then
    verify(userRepository, times(1)).deleteByUuid(testUser.getUuid());
    verify(userRepository, times(1)).deleteByUuid(secondUuid);
    verify(userRepository, times(2)).deleteByUuid(any(UUID.class));
  }

  @Test
  void delete_WhenAuthorizedUserIsNotAdmin_ShouldThrowForbidden() {
    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUser.getUuid(), authenticatedTestUser);
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Only admin users can delete users");

    verify(userRepository, never()).deleteByUuid(any(UUID.class));
  }
}
