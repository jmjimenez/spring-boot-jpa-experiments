package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindUserByUuidTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FindUserByUuid findUserByUuid;

  private User testUser;
  private User adminUser;
  private UUID testUuid;
  private JwtUserDetails adminUserDetails;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    adminUser = UserFactory.createUser(UUID.randomUUID(), "Admin User", "admin@example.com", "admin");
    adminUserDetails = UserDetailsFactory.createJwtUserDetails(adminUser);
  }

  @Test
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // Given
    when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUuid, adminUserDetails);

    // When
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    assertThat(result.get().getUuid()).isEqualTo(testUuid);
    verify(userRepository, times(1)).findByUuid(testUuid);
  }

  @Test
  void findByUuid_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // Given
    UUID nonExistentUuid = UUID.randomUUID();
    when(userRepository.findByUuid(nonExistentUuid)).thenReturn(Optional.empty());
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(nonExistentUuid, adminUserDetails);

    // When
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByUuid(nonExistentUuid);
  }

  @Test
  void findByUuid_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByUuid(testUuid))
        .thenThrow(new RuntimeException("Database error"));
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUuid, adminUserDetails);

    // When & Then
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findByUuid(testUuid);
  }

  @Test
  void findByUuid_WhenUserIsNeitherAdminNorSameUuid_ShouldThrowForbiddenException() {
    // Given
    UUID differentUuid = UUID.randomUUID();
    var regularUser = UserFactory.createUser("Regular User", "regular@example.com", "regularuser");
    JwtUserDetails regularUserDetails = UserDetailsFactory.createJwtUserDetails(regularUser);
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(differentUuid, regularUserDetails);

    // When & Then
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Access denied: only admins or the user themselves can view user data");

    // Verify that repository is never called
    verify(userRepository, never()).findByUuid(any());
  }
}
