package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.Optional;

import es.jmjg.experiments.domain.user.exception.UserNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.application.user.dto.AuthenticatedUserDto;
import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindUserByUuidTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FindUserByUuid findUserByUuid;

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
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // Given
    when(userRepository.findByUserUuid(testUser.getUserUuid())).thenReturn(Optional.of(testUser));
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUser.getUserUuid(), authenticatedAdminUser);

    // When
    User result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isEqualTo(testUser);
    assertThat(result.getName()).isEqualTo(testUser.getName());
    assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
    assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
    assertThat(result.getUserUuid()).isEqualTo(testUser.getUserUuid());
    verify(userRepository, times(1)).findByUserUuid(testUser.getUserUuid());
  }

  @Test
  void findByUuid_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // Given
    UserUuid nonExistentUuid = UserUuid.randomUUID();
    when(userRepository.findByUserUuid(nonExistentUuid)).thenReturn(Optional.empty());
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(nonExistentUuid, authenticatedAdminUser);

    // When
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto)).isInstanceOf(UserNotFound.class);

    // Then
    verify(userRepository, times(1)).findByUserUuid(nonExistentUuid);
  }

  @Test
  void findByUuid_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByUserUuid(testUser.getUserUuid()))
        .thenThrow(new RuntimeException("Database error"));
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUser.getUserUuid(), authenticatedAdminUser);

    // When & Then
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findByUserUuid(testUser.getUserUuid());
  }

  @Test
  void findByUuid_WhenUserIsNeitherAdminNorSameUuid_ShouldThrowForbiddenException() {
    // Given
    UserUuid differentUuid = UserUuid.randomUUID();

    // When & Then
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(differentUuid, authenticatedTestUser);
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Access denied: only admins or the user themselves can view user data");

    // Verify that repository is never called
    verify(userRepository, never()).findByUserUuid(any());
  }
}
