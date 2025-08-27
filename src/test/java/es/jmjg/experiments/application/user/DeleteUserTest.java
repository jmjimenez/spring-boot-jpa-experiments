package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.user.dto.DeleteUserDto;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class DeleteUserTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DeleteUser deleteUser;

  private UUID testUuid;
  private User testUser;
  private JwtUserDetails testUserDetails;
  private JwtUserDetails adminUserDetails;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    var adminUser = UserFactory.createUser(UUID.randomUUID(), "Admin User", "admin@example.com", "admin");
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);
    adminUserDetails = UserDetailsFactory.createJwtUserDetails(adminUser);
  }

  @Test
  void delete_WhenUserExists_ShouldDeleteUser() {
    // Given
    when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
    doNothing().when(userRepository).deleteByUuid(testUuid);
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid, adminUserDetails);

    // When
    deleteUser.delete(deleteUserDto);

    // Then
    verify(userRepository, times(1)).findByUuid(testUuid);
    verify(userRepository, times(1)).deleteByUuid(testUuid);
  }

  @Test
  void delete_WhenUserDoesNotExist_ShouldThrowUserNotFound() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    DeleteUserDto deleteUserDto = new DeleteUserDto(nonExistentUuid, adminUserDetails);

    // When & Then
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(UserNotFound.class)
        .hasMessage("User not found with uuid: " + nonExistentUuid);
    verify(userRepository, never()).deleteByUuid(nonExistentUuid);
  }

  @Test
  void delete_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
    doThrow(new RuntimeException("Database error")).when(userRepository).deleteByUuid(testUuid);
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid, adminUserDetails);

    // When & Then
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).deleteByUuid(testUuid);
  }

  @Test
  void delete_WhenCalledMultipleTimes_ShouldCallRepositoryEachTime() {
    // Given
    UUID secondUuid = UUID.randomUUID();
    when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
    when(userRepository.findByUuid(secondUuid)).thenReturn(Optional.of(testUser));
    doNothing().when(userRepository).deleteByUuid(any(UUID.class));
    DeleteUserDto deleteUserDto1 = new DeleteUserDto(testUuid, adminUserDetails);
    DeleteUserDto deleteUserDto2 = new DeleteUserDto(secondUuid, adminUserDetails);

    // When
    deleteUser.delete(deleteUserDto1);
    deleteUser.delete(deleteUserDto2);

    // Then
    verify(userRepository, times(1)).deleteByUuid(testUuid);
    verify(userRepository, times(1)).deleteByUuid(secondUuid);
    verify(userRepository, times(2)).deleteByUuid(any(UUID.class));
  }

  @Test
  void delete_WhenAuthorizedUserIsNotAdmin_ShouldThrowForbidden() {
    // Given
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid, testUserDetails);

    // When & Then
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(es.jmjg.experiments.application.shared.exception.Forbidden.class)
        .hasMessage("Only admin users can delete users");

    verify(userRepository, never()).deleteByUuid(any(UUID.class));
  }
}
