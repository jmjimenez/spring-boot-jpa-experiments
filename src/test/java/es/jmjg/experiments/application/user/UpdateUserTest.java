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

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class UpdateUserTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UpdateUser updateUser;

  private User existingUser;
  private User updateData;

  @BeforeEach
  void setUp() {
    existingUser = UserFactory.createUser(1, UUID.randomUUID(), "Old Name", "old@example.com", "olduser");
    updateData = UserFactory.createUser("New Name", "new@example.com", "newuser");
    updateData.setUuid(null);
  }

  @Test
  void update_WhenUserExists_ShouldUpdateFields() {
    when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = updateUser.update(1, updateData);

    assertThat(result.getName()).isEqualTo("New Name");
    assertThat(result.getEmail()).isEqualTo("new@example.com");
    assertThat(result.getUsername()).isEqualTo("newuser");
    assertThat(result.getUuid()).isEqualTo(existingUser.getUuid());
    verify(userRepository).save(existingUser);
  }

  @Test
  void update_WhenUserExistsAndUuidProvided_ShouldUpdateUuid() {
    UUID newUuid = UUID.randomUUID();
    updateData.setUuid(newUuid);
    when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = updateUser.update(1, updateData);

    assertThat(result.getUuid()).isEqualTo(newUuid);
    verify(userRepository).save(existingUser);
  }

  @Test
  void update_WhenUserDoesNotExist_ShouldThrow() {
    when(userRepository.findById(99)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> updateUser.update(99, updateData))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found with id: 99");
    verify(userRepository, never()).save(any());
  }
}