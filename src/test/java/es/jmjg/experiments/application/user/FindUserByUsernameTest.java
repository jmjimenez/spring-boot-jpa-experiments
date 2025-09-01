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

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByUsernameDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindUserByUsernameTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FindUserByUsername findUserByUsername;

  private User testUser;
  private JwtUserDetails testUserDetails;
  private JwtUserDetails adminUserDetails;

  @BeforeEach
  void setUp() {
    testUser = UserFactory.createBasicUser();
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);
    var adminUser = UserFactory.createAdminUser();
    adminUserDetails = UserDetailsFactory.createJwtUserDetails(adminUser);
  }

  @Test
  void findByUsername_WhenUserExists_ShouldReturnUser() {
    // Given
    when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUser.getUsername(), adminUserDetails);

    // When
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    assertThat(result.get().getName()).isEqualTo(testUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(testUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(testUser.getUsername());
    verify(userRepository, times(1)).findByUsername(testUser.getUsername());
  }

  @Test
  void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // Given
    String nonExistentUsername = "nonexistentuser";
    when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(nonExistentUsername, adminUserDetails);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByUsername(nonExistentUsername);
  }

  @Test
  void findByUsername_WhenUsernameIsEmpty_ShouldReturnEmpty() {
    // Given
    var emptyUsername = "";
    when(userRepository.findByUsername(emptyUsername)).thenReturn(Optional.empty());

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(emptyUsername, adminUserDetails);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByUsername(emptyUsername);
  }

  @Test
  void findByUsername_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByUsername(testUser.getUsername()))
        .thenThrow(new RuntimeException("Database error"));

    // When & Then
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUser.getUsername(), adminUserDetails);
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findByUsername(testUser.getUsername());
  }

  @Test
  void findByUsername_WhenUsernameIsBlank_ShouldReturnEmpty() {
    // Given
    when(userRepository.findByUsername("   ")).thenReturn(Optional.empty());

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto("   ", adminUserDetails);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByUsername("   ");
  }

  @Test
  void findByUsername_WhenAuthenticatedUserIsTestUserAndUsernameIsHis_ShouldReturnUser() {
    // Given
    when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

    // When
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(testUser.getUsername(), testUserDetails);
    Optional<User> result = findUserByUsername.findByUsername(findUserByUsernameDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    assertThat(result.get().getName()).isEqualTo(testUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(testUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(testUser.getUsername());
    verify(userRepository, times(1)).findByUsername(testUser.getUsername());
  }

  @Test
  void findByUsername_WhenAuthenticatedUserIsTestUserAndUsernameIsNotHis_ShouldThrowForbidden() {
    // Given
    String otherUsername = "otheruser";

    // When & Then
    FindUserByUsernameDto findUserByUsernameDto = new FindUserByUsernameDto(otherUsername, testUserDetails);
    assertThatThrownBy(() -> findUserByUsername.findByUsername(findUserByUsernameDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Access denied: only admins or the user themselves can view user data");
    verify(userRepository, never()).findByUsername(any());
  }
}
