package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

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
class FindUserByEmailTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FindUserByEmail findUserByEmail;

  private User testUser;
  private String testEmail;

  @BeforeEach
  void setUp() {
    testEmail = "test@example.com";
    testUser = UserFactory.createUser("Test User", testEmail, "testuser");
  }

  @Test
  void findByEmail_WhenUserExists_ShouldReturnUser() {
    // Given
    when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(testEmail);

    // When
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo(testEmail);
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    verify(userRepository, times(1)).findByEmail(testEmail);
  }

  @Test
  void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // Given
    String nonExistentEmail = "nonexistent@example.com";
    when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(nonExistentEmail);

    // When
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByEmail(nonExistentEmail);
  }

  @Test
  void findByEmail_WhenEmailIsNull_ShouldReturnEmpty() {
    // Given
    when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(null);

    // When
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByEmail(null);
  }

  @Test
  void findByEmail_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByEmail(testEmail))
        .thenThrow(new RuntimeException("Database error"));
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(testEmail);

    // When & Then
    assertThatThrownBy(() -> findUserByEmail.findByEmail(findUserByEmailDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findByEmail(testEmail);
  }

  @Test
  void findByEmail_WhenEmailIsEmpty_ShouldReturnEmpty() {
    // Given
    String emptyEmail = "";
    when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(emptyEmail);

    // When
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByEmail(emptyEmail);
  }

  @Test
  void findByEmail_WhenEmailIsBlank_ShouldReturnEmpty() {
    // Given
    String blankEmail = "   ";
    when(userRepository.findByEmail(blankEmail)).thenReturn(Optional.empty());
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(blankEmail);

    // When
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByEmail(blankEmail);
  }

  @Test
  void findByEmail_WhenEmailHasDifferentCase_ShouldReturnEmpty() {
    // Given
    String upperCaseEmail = "TEST@EXAMPLE.COM";
    when(userRepository.findByEmail(upperCaseEmail)).thenReturn(Optional.empty());
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(upperCaseEmail);

    // When
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByEmail(upperCaseEmail);
  }

  @Test
  void findByEmail_WhenEmailHasSpecialCharacters_ShouldReturnUser() {
    // Given
    String specialEmail = "test+tag@example.com";
    User specialUser = UserFactory.createUser("Special User", specialEmail, "specialuser");
    when(userRepository.findByEmail(specialEmail)).thenReturn(Optional.of(specialUser));
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(specialEmail);

    // When
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(specialUser);
    assertThat(result.get().getEmail()).isEqualTo(specialEmail);
    verify(userRepository, times(1)).findByEmail(specialEmail);
  }
}
