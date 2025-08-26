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

import es.jmjg.experiments.application.user.dto.UpdateUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class UpdateUserTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UpdateUser updateUser;

  private UUID testUuid;
  private JwtUserDetails testUserDetails;
  private User existingUser;
  private UpdateUserDto updateUserDto;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    var testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createUserUserDetails(testUser);
    existingUser = UserFactory.createUser(1, testUuid, "Old Name", "old@example.com", "olduser");
    updateUserDto = new UpdateUserDto(
        1,
        null,
        "New Name",
        "new@example.com",
        "newuser",
        null,
        testUserDetails);
  }

  @Test
  void update_WhenUserExists_ShouldUpdateFields() {
    // Given
    when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = updateUser.update(updateUserDto);

    // Then
    assertThat(result.getName()).isEqualTo("New Name");
    assertThat(result.getEmail()).isEqualTo("new@example.com");
    assertThat(result.getUsername()).isEqualTo("newuser");
    assertThat(result.getUuid()).isEqualTo(testUuid);
    verify(userRepository, times(1)).save(existingUser);
  }

  @Test
  void update_WhenUserExistsAndUuidProvided_ShouldUpdateUuid() {
    // Given
    UUID newUuid = UUID.randomUUID();
    updateUserDto.setUuid(newUuid);
    when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = updateUser.update(updateUserDto);

    // Then
    assertThat(result.getUuid()).isEqualTo(newUuid);
    verify(userRepository, times(1)).save(existingUser);
  }

  @Test
  void update_WhenUserDoesNotExist_ShouldThrow() {
    // Given
    updateUserDto.setId(99);
    when(userRepository.findById(99)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> updateUser.update(updateUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found with id: 99");
    verify(userRepository, never()).save(any());
  }
}