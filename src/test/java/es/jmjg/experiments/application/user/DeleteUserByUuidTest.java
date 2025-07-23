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
import es.jmjg.experiments.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DeleteUserByUuidTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DeleteUserByUuid deleteUserByUuid;

  private UUID testUuid;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
  }

  @Test
  void deleteByUuid_WhenUserExists_ShouldDeleteUser() {
    // Given
    doNothing().when(userRepository).deleteByUuid(testUuid);

    // When
    deleteUserByUuid.deleteByUuid(testUuid);

    // Then
    verify(userRepository, times(1)).deleteByUuid(testUuid);
  }

  @Test
  void deleteByUuid_WhenUserDoesNotExist_ShouldNotThrowException() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    doNothing().when(userRepository).deleteByUuid(nonExistentUuid);

    // When & Then
    assertThatCode(() -> deleteUserByUuid.deleteByUuid(nonExistentUuid))
        .doesNotThrowAnyException();
    verify(userRepository, times(1)).deleteByUuid(nonExistentUuid);
  }

  @Test
  void deleteByUuid_WhenUuidIsNull_ShouldNotThrowException() {
    // Given
    doNothing().when(userRepository).deleteByUuid(null);

    // When & Then
    assertThatCode(() -> deleteUserByUuid.deleteByUuid(null))
        .doesNotThrowAnyException();
    verify(userRepository, times(1)).deleteByUuid(null);
  }

  @Test
  void deleteByUuid_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    doThrow(new RuntimeException("Database error")).when(userRepository).deleteByUuid(testUuid);

    // When & Then
    assertThatThrownBy(() -> deleteUserByUuid.deleteByUuid(testUuid))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).deleteByUuid(testUuid);
  }

  @Test
  void deleteByUuid_WhenCalledMultipleTimes_ShouldCallRepositoryEachTime() {
    // Given
    UUID secondUuid = UUID.randomUUID();
    doNothing().when(userRepository).deleteByUuid(any(UUID.class));

    // When
    deleteUserByUuid.deleteByUuid(testUuid);
    deleteUserByUuid.deleteByUuid(secondUuid);

    // Then
    verify(userRepository, times(1)).deleteByUuid(testUuid);
    verify(userRepository, times(1)).deleteByUuid(secondUuid);
    verify(userRepository, times(2)).deleteByUuid(any(UUID.class));
  }
}
