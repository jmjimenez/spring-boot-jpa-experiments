package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
  private UUID testUuid;
  private JwtUserDetails testUserDetails;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    var testUserForDetails = UserFactory.createUser("Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUserForDetails);
  }

  @Test
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // Given
    when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUuid, testUserDetails);

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
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(nonExistentUuid, testUserDetails);

    // When
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findByUuid(nonExistentUuid);
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldReturnEmpty() {
    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(null, testUserDetails);
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, never()).findByUuid(any());
  }

  @Test
  void findByUuid_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findByUuid(testUuid))
        .thenThrow(new RuntimeException("Database error"));
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUuid, testUserDetails);

    // When & Then
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findByUuid(testUuid);
  }
}
