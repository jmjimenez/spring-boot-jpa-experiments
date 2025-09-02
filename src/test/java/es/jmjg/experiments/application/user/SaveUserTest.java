package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class SaveUserTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private SaveUser saveUser;

  private User testUser;
  private AuthenticatedUserDto authenticatedTestUser;
  private AuthenticatedUserDto authenticatedAdminUser;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    authenticatedTestUser = UserDetailsFactory.createAuthenticatedUserDto(testUser);

    var adminUser = UserFactory.createAdminUser();
    authenticatedAdminUser = UserDetailsFactory.createAuthenticatedUserDto(adminUser);
  }

  @Test
  void save_WhenAuthenticatedUserIsAdmin_ShouldSaveAndReturnUser() {
    // Given
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    var saveUserDto = new SaveUserDto(
        testUser.getUuid(),
        testUser.getName(),
        testUser.getEmail(),
        testUser.getUsername(),
        testUser.getPassword(),
        authenticatedAdminUser);
    User result = saveUser.save(saveUserDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(testUser.getId());
    assertThat(result.getName()).isEqualTo(testUser.getName());
    assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
    assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
    assertThat(result.getUuid()).isEqualTo(testUser.getUuid());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void save_WhenAuthenticatedUserIsNotAdmin_ShouldThrowForbiddenException() {
    // When & Then
    var saveUserDto = new SaveUserDto(
        testUser.getUuid(),
        testUser.getName(),
        testUser.getEmail(),
        testUser.getUsername(),
        testUser.getPassword(),
        authenticatedTestUser);
    assertThatThrownBy(() -> saveUser.save(saveUserDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Only administrators can create users");
  }

  @Test
  void save_WhenUserHasValidData_ShouldSaveAndReturnUser() {
    // Given
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    var saveUserDto = new SaveUserDto(
        testUser.getUuid(),
        testUser.getName(),
        testUser.getEmail(),
        testUser.getUsername(),
        testUser.getPassword(),
        authenticatedAdminUser);
    User result = saveUser.save(saveUserDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(testUser.getId());
    assertThat(result.getUuid()).isEqualTo(testUser.getUuid());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void save_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.save(any(User.class)))
        .thenThrow(new RuntimeException("Database error"));

    // When & Then
    var saveUserDto = new SaveUserDto(
        testUser.getUuid(),
        testUser.getName(),
        testUser.getEmail(),
        testUser.getUsername(),
        testUser.getPassword(),
        authenticatedAdminUser);
    assertThatThrownBy(() -> saveUser.save(saveUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).save(any(User.class));
  }
}