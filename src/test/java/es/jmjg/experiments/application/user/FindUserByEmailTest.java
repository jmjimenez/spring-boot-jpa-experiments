package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByEmailDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindUserByEmailTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FindUserByEmail findUserByEmail;

  private User testUser;
  private AuthenticatedUserDto authenticatedTestUser;
  private AuthenticatedUserDto authenticatedAdminUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    authenticatedTestUser = AuthenticatedUserFactory.createAuthenticatedUserDto(testUser);
    var adminUser = UserFactory.createAdminUser();
    authenticatedAdminUser = AuthenticatedUserFactory.createAuthenticatedUserDto(adminUser);
  }

  @Test
  void findByEmail_WhenUserExists_ShouldReturnUser() {
    // Given
    when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(testUser.getEmail(), authenticatedAdminUser);
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo(testUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    verify(userRepository, times(1)).findByEmail(testUser.getEmail());
  }

  @Test
  void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // Given
    String nonExistentEmail = "nonexistent@example.com";
    when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(nonExistentEmail, authenticatedAdminUser);
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByEmail(nonExistentEmail);
  }

  @Test
  void findByEmail_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByEmail(testUser.getEmail()))
        .thenThrow(new RuntimeException("Database error"));

    // When & Then
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(testUser.getEmail(), authenticatedAdminUser);
    assertThatThrownBy(() -> findUserByEmail.findByEmail(findUserByEmailDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");

    verify(userRepository, times(1)).findByEmail(testUser.getEmail());
  }

  @Test
  void findByEmail_WhenEmailIsEmpty_ShouldReturnEmpty() {
    // Given
    String emptyEmail = "";
    when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(emptyEmail, authenticatedAdminUser);
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

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(blankEmail, authenticatedAdminUser);
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByEmail(blankEmail);
  }

  @Test
  void findByEmail_WhenEmailHasDifferentCase_ShouldReturnEmpty() {
    // Given
    String upperCaseEmail = testUser.getEmail().toUpperCase();
    when(userRepository.findByEmail(upperCaseEmail)).thenReturn(Optional.empty());

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(upperCaseEmail, authenticatedAdminUser);
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByEmail(upperCaseEmail);
  }

  @Test
  void findByEmail_WhenAuthenticatedUserIsNotAdmin_ShouldThrowForbidden() {
    // When & Then
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(testUser.getEmail(), authenticatedTestUser);
    assertThatThrownBy(() -> findUserByEmail.findByEmail(findUserByEmailDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Only admin users can search users by email");
    verify(userRepository, never()).findByEmail(any());
  }

  @Test
  void findByEmail_WhenEmailHasSpecialCharacters_ShouldReturnUser() {
    // Given
    String specialEmail = "test+tag@example.com";
    User specialUser = UserFactory.createUser("Special User", specialEmail, "specialuser");
    when(userRepository.findByEmail(specialEmail)).thenReturn(Optional.of(specialUser));

    // When
    FindUserByEmailDto findUserByEmailDto = new FindUserByEmailDto(specialEmail, authenticatedAdminUser);
    Optional<User> result = findUserByEmail.findByEmail(findUserByEmailDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(specialUser);
    assertThat(result.get().getEmail()).isEqualTo(specialEmail);
    verify(userRepository, times(1)).findByEmail(specialEmail);
  }
}
