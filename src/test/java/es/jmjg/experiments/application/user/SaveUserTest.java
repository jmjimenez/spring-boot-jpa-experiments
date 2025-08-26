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

import es.jmjg.experiments.application.user.dto.SaveUserDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class SaveUserTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private SaveUser saveUser;

  private SaveUserDto saveUserDto;

  @BeforeEach
  void setUp() {
    User userToSave = UserFactory.createUser("Test User", "test@example.com", "testuser");
    saveUserDto = new SaveUserDto(
        userToSave.getUuid(),
        userToSave.getName(),
        userToSave.getEmail(),
        userToSave.getUsername(),
        userToSave.getPassword());
  }

  @Test
  void save_WhenUserIsValid_ShouldSaveAndReturnUser() {
    User savedUser = UserFactory.createUser(1, saveUserDto.getUuid(), "Test User",
        "test@example.com", "testuser");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    User result = saveUser.save(saveUserDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("Test User");
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    assertThat(result.getUsername()).isEqualTo("testuser");
    assertThat(result.getUuid()).isEqualTo(saveUserDto.getUuid());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void save_WhenUserHasValidData_ShouldSaveAndReturnUser() {
    User savedUser = UserFactory.createUser(1, saveUserDto.getUuid(), "Test User",
        "test@example.com", "testuser");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    User result = saveUser.save(saveUserDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getUuid()).isEqualTo(saveUserDto.getUuid());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void save_WhenUserHasNoId_ShouldSaveAndReturnUserWithGeneratedId() {
    User savedUser = UserFactory.createUser(1, saveUserDto.getUuid(), "Test User",
        "test@example.com", "testuser");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    User result = saveUser.save(saveUserDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void save_WhenRepositoryThrowsException_ShouldPropagateException() {
    when(userRepository.save(any(User.class)))
        .thenThrow(new RuntimeException("Database error"));

    assertThatThrownBy(() -> saveUser.save(saveUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository).save(any(User.class));
  }
}