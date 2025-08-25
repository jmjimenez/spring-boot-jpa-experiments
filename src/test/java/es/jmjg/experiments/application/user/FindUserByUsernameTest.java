package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindUserByUsernameTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FindUserByUsername findUserByUsername;

  private User testUser;
  private String testUsername;

  @BeforeEach
  void setUp() {
    testUsername = "testuser";
    testUser = UserFactory.createUser(UUID.randomUUID(), "Test User", "test@example.com", testUsername);
  }

  @Test
  void findByUsername_WhenUserExists_ShouldReturnUser() {
    // Given
    when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUsername);

    // When
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo(testUsername);
    verify(userRepository, times(1)).findByUsername(testUsername);
  }

  @Test
  void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // Given
    String nonExistentUsername = "nonexistentuser";
    when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(nonExistentUsername);

    // When
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByUsername(nonExistentUsername);
  }

  @Test
  void findByUsername_WhenUsernameIsNull_ShouldReturnEmpty() {
    // Given
    when(userRepository.findByUsername(null)).thenReturn(Optional.empty());
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(null);

    // When
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByUsername(null);
  }

  @Test
  void findByUsername_WhenUsernameIsEmpty_ShouldReturnEmpty() {
    // Given
    when(userRepository.findByUsername("")).thenReturn(Optional.empty());
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("");

    // When
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByUsername("");
  }

  @Test
  void findByUsername_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByUsername(testUsername))
        .thenThrow(new RuntimeException("Database error"));
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUsername);

    // When & Then
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findByUsername(testUsername);
  }

  @Test
  void findByUsername_WhenUsernameIsBlank_ShouldReturnEmpty() {
    // Given
    when(userRepository.findByUsername("   ")).thenReturn(Optional.empty());
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("   ");

    // When
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByUsername("   ");
  }
}
