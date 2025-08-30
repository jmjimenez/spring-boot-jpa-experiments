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
import org.springframework.security.crypto.password.PasswordEncoder;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
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

  private UUID testUuid;
  private SaveUserDto saveUserByAdminDto;
  private SaveUserDto saveUserByUserDto;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    var testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    var testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);
    saveUserByUserDto = new SaveUserDto(
        testUser.getUuid(),
        testUser.getName(),
        testUser.getEmail(),
        testUser.getUsername(),
        testUser.getPassword(),
        testUserDetails);
    
    var adminUser = UserFactory.createUser(UUID.randomUUID(), "Admin User", "admin@example.com", "admin");
    var adminUserDetails = UserDetailsFactory.createJwtUserDetails(adminUser);
    saveUserByAdminDto = new SaveUserDto(
        adminUser.getUuid(),
        adminUser.getName(),
        adminUser.getEmail(),
        adminUser.getUsername(),
        adminUser.getPassword(),
        adminUserDetails);
  }

  @Test
  void save_WhenAuthenticatedUserIsAdmin_ShouldSaveAndReturnUser() {
    // Given
    User savedUser = UserFactory.createUser(1, testUuid, "Test User",
        "test@example.com", "testuser");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // When
    User result = saveUser.save(saveUserByAdminDto);

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
  void save_WhenAuthenticatedUserIsNotAdmin_ShouldThrowForbiddenException() {
    // When & Then
    assertThatThrownBy(() -> saveUser.save(saveUserByUserDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Only administrators can create users");
  }

  @Test
  void save_WhenUserHasValidData_ShouldSaveAndReturnUser() {
    // Given
    User savedUser = UserFactory.createUser(1, testUuid, "Test User",
        "test@example.com", "testuser");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // When
    User result = saveUser.save(saveUserByAdminDto);

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
    User result = saveUser.save(saveUserByAdminDto);

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
    assertThatThrownBy(() -> saveUser.save(saveUserByAdminDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).save(any(User.class));
  }
}