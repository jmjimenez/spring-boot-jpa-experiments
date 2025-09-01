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

  private User testUser;
  private JwtUserDetails testUserDetails;
  private JwtUserDetails adminUserDetails;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    var adminUser = UserFactory.createAdminUser();
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);
    adminUserDetails = UserDetailsFactory.createJwtUserDetails(adminUser);
  }

  @Test
  void delete_WhenUserExists_ShouldDeleteUser() {
    // Given
    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    doNothing().when(userRepository).deleteByUuid(testUser.getUuid());

    // When
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUser.getUuid(), adminUserDetails);
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
    DeleteUserDto deleteUserDto = new DeleteUserDto(nonExistentUuid, adminUserDetails);
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(UserNotFound.class)
        .hasMessage("User not found with uuid: " + nonExistentUuid.toString());
    verify(userRepository, never()).deleteByUuid(nonExistentUuid);
  }

  @Test
  void delete_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    doThrow(new RuntimeException("Database error")).when(userRepository).deleteByUuid(testUser.getUuid());

    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUser.getUuid(), adminUserDetails);
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
    DeleteUserDto deleteUserDto1 = new DeleteUserDto(testUser.getUuid(), adminUserDetails);
    deleteUser.delete(deleteUserDto1);
    DeleteUserDto deleteUserDto2 = new DeleteUserDto(secondUuid, adminUserDetails);
    deleteUser.delete(deleteUserDto2);

    // Then
    verify(userRepository, times(1)).deleteByUuid(testUser.getUuid());
    verify(userRepository, times(1)).deleteByUuid(secondUuid);
    verify(userRepository, times(2)).deleteByUuid(any(UUID.class));
  }

  @Test
  void delete_WhenAuthorizedUserIsNotAdmin_ShouldThrowForbidden() {
    // When & Then
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUser.getUuid(), testUserDetails);
    assertThatThrownBy(() -> deleteUser.delete(deleteUserDto))
        .isInstanceOf(es.jmjg.experiments.application.shared.exception.Forbidden.class)
        .hasMessage("Only admin users can delete users");

    verify(userRepository, never()).deleteByUuid(any(UUID.class));
  }
}
