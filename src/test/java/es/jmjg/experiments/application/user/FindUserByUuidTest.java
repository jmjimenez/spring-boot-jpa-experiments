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
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // Given
    when(userRepository.findByUuid(testUser.getUuid())).thenReturn(Optional.of(testUser));
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUser.getUuid(), adminUserDetails);

    // When
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    assertThat(result.get().getName()).isEqualTo(testUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(testUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(testUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(testUser.getUuid());
    verify(userRepository, times(1)).findByUuid(testUser.getUuid());
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
    when(userRepository.findByUuid(testUser.getUuid()))
        .thenThrow(new RuntimeException("Database error"));
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUser.getUuid(), adminUserDetails);

    // When & Then
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findByUuid(testUser.getUuid());
  }

  @Test
  void findByUuid_WhenUserIsNeitherAdminNorSameUuid_ShouldThrowForbiddenException() {
    // Given
    UUID differentUuid = UUID.randomUUID();
    // var regularUser = UserFactory.createUser("Regular User", "regular@example.com", "regularuser");
    // JwtUserDetails regularUserDetails = UserDetailsFactory.createJwtUserDetails(regularUser);
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(differentUuid, testUserDetails);

    // When & Then
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Access denied: only admins or the user themselves can view user data");

    // Verify that repository is never called
    verify(userRepository, never()).findByUuid(any());
  }
}
