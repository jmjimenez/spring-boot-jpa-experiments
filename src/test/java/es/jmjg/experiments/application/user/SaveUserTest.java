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

import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class SaveUserTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private SaveUser saveUser;

  private UUID testUuid;
  private JwtUserDetails testUserDetails;
  private SaveUserDto saveUserDto;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    var testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createUserUserDetails(testUser);
    saveUserDto = new SaveUserDto(
        testUser.getUuid(),
        testUser.getName(),
        testUser.getEmail(),
        testUser.getUsername(),
        testUser.getPassword(),
        testUserDetails);
  }

  @Test
  void save_WhenUserIsValid_ShouldSaveAndReturnUser() {
    // Given
    User savedUser = UserFactory.createUser(1, testUuid, "Test User",
        "test@example.com", "testuser");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // When
    User result = saveUser.save(saveUserDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("Test User");
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    assertThat(result.getUsername()).isEqualTo("testuser");
    assertThat(result.getUuid()).isEqualTo(testUuid);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void save_WhenUserHasValidData_ShouldSaveAndReturnUser() {
    // Given
    User savedUser = UserFactory.createUser(1, testUuid, "Test User",
        "test@example.com", "testuser");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // When
    User result = saveUser.save(saveUserDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getUuid()).isEqualTo(testUuid);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void save_WhenUserHasNoId_ShouldSaveAndReturnUserWithGeneratedId() {
    // Given
    User savedUser = UserFactory.createUser(1, testUuid, "Test User",
        "test@example.com", "testuser");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // When
    User result = saveUser.save(saveUserDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void save_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.save(any(User.class)))
        .thenThrow(new RuntimeException("Database error"));

    // When & Then
    assertThatThrownBy(() -> saveUser.save(saveUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).save(any(User.class));
  }
}