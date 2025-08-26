package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.user.dto.DeleteUserDto;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class DeleteUserByUuidTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DeleteUser deleteUser;

  private UUID testUuid;
  private JwtUserDetails testUserDetails;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    var testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createUserUserDetails(testUser);
  }

  @Test
  void deleteByUuid_WhenUserExists_ShouldDeleteUser() {
    // Given
    doNothing().when(userRepository).deleteByUuid(testUuid);
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid, testUserDetails);

    // When
    deleteUser.deleteByUuid(deleteUserDto);

    // Then
    verify(userRepository, times(1)).deleteByUuid(testUuid);
  }

  @Test
  void deleteByUuid_WhenUserDoesNotExist_ShouldNotThrowException() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    doNothing().when(userRepository).deleteByUuid(nonExistentUuid);
    DeleteUserDto deleteUserDto = new DeleteUserDto(nonExistentUuid, testUserDetails);

    // When & Then
    assertThatCode(() -> deleteUser.deleteByUuid(deleteUserDto))
        .doesNotThrowAnyException();
    verify(userRepository, times(1)).deleteByUuid(nonExistentUuid);
  }

  @Test
  void deleteByUuid_WhenUuidIsNull_ShouldNotThrowException() {
    // Given
    doNothing().when(userRepository).deleteByUuid(null);
    DeleteUserDto deleteUserDto = new DeleteUserDto(null, testUserDetails);

    // When & Then
    assertThatCode(() -> deleteUser.deleteByUuid(deleteUserDto))
        .doesNotThrowAnyException();
    verify(userRepository, times(1)).deleteByUuid(null);
  }

  @Test
  void deleteByUuid_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    doThrow(new RuntimeException("Database error")).when(userRepository).deleteByUuid(testUuid);
    DeleteUserDto deleteUserDto = new DeleteUserDto(testUuid, testUserDetails);

    // When & Then
    assertThatThrownBy(() -> deleteUser.deleteByUuid(deleteUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).deleteByUuid(testUuid);
  }

  @Test
  void deleteByUuid_WhenCalledMultipleTimes_ShouldCallRepositoryEachTime() {
    // Given
    UUID secondUuid = UUID.randomUUID();
    doNothing().when(userRepository).deleteByUuid(any(UUID.class));
    DeleteUserDto deleteUserDto1 = new DeleteUserDto(testUuid, testUserDetails);
    DeleteUserDto deleteUserDto2 = new DeleteUserDto(secondUuid, testUserDetails);

    // When
    deleteUser.deleteByUuid(deleteUserDto1);
    deleteUser.deleteByUuid(deleteUserDto2);

    // Then
    verify(userRepository, times(1)).deleteByUuid(testUuid);
    verify(userRepository, times(1)).deleteByUuid(secondUuid);
    verify(userRepository, times(2)).deleteByUuid(any(UUID.class));
  }
}
