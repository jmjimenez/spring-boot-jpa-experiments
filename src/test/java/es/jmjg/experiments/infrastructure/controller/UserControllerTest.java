package es.jmjg.experiments.infrastructure.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import es.jmjg.experiments.application.user.DeleteUserByUuid;
import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.FindUserByEmail;
import es.jmjg.experiments.application.user.FindUserById;
import es.jmjg.experiments.application.user.FindUserByUsername;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.SaveUser;
import es.jmjg.experiments.application.user.UpdateUser;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.controller.dto.UserRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.UserResponseDto;
import es.jmjg.experiments.infrastructure.controller.exception.UserNotFoundException;
import es.jmjg.experiments.infrastructure.controller.mapper.UserMapper;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private UserMapper userMapper;

  @Mock
  private SaveUser saveUser;

  @Mock
  private UpdateUser updateUser;

  @Mock
  private FindUserById findUserById;

  @Mock
  private FindUserByUuid findUserByUuid;

  @Mock
  private FindUserByEmail findUserByEmail;

  @Mock
  private FindUserByUsername findUserByUsername;

  @Mock
  private FindAllUsers findAllUsers;

  @Mock
  private DeleteUserByUuid deleteUserByUuid;

  @InjectMocks
  private UserController userController;

  private User testUser;
  private UserRequestDto userRequestDto;
  private UserResponseDto userResponseDto;
  private UUID testUuid;
  private Integer testId;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();
    testId = 1;
    testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    testUser.setId(testId);

    userRequestDto = new UserRequestDto(testUuid, "Test User", "test@example.com", "testuser");
    userResponseDto = new UserResponseDto(testUuid, "Test User", "test@example.com", "testuser");
  }

  @Test
  void findAll_ShouldReturnAllUsers() {
    // Given
    List<User> users = List.of(testUser);
    List<UserResponseDto> expectedResponse = List.of(userResponseDto);
    when(findAllUsers.findAll()).thenReturn(users);
    when(userMapper.toResponseDtoList(users)).thenReturn(expectedResponse);

    // When
    List<UserResponseDto> result = userController.findAll();

    // Then
    assertThat(result).isEqualTo(expectedResponse);
    verify(findAllUsers).findAll();
    verify(userMapper).toResponseDtoList(users);
  }

  @Test
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // Given
    when(findUserByUuid.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
    when(userMapper.toResponseDto(testUser)).thenReturn(userResponseDto);

    // When
    UserResponseDto result = userController.findByUuid(testUuid);

    // Then
    assertThat(result).isEqualTo(userResponseDto);
    verify(findUserByUuid).findByUuid(testUuid);
    verify(userMapper).toResponseDto(testUser);
  }

  @Test
  void findByUuid_WhenUserDoesNotExist_ShouldThrowException() {
    // Given
    when(findUserByUuid.findByUuid(testUuid)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userController.findByUuid(testUuid))
        .isInstanceOf(UserNotFoundException.class);
    verify(findUserByUuid).findByUuid(testUuid);
    verify(userMapper, never()).toResponseDto(any());
  }

  @Test
  void findByEmail_WhenUserExists_ShouldReturnUser() {
    // Given
    String email = "test@example.com";
    when(findUserByEmail.findByEmail(email)).thenReturn(Optional.of(testUser));
    when(userMapper.toResponseDto(testUser)).thenReturn(userResponseDto);

    // When
    UserResponseDto result = userController.findByEmail(email);

    // Then
    assertThat(result).isEqualTo(userResponseDto);
    verify(findUserByEmail).findByEmail(email);
    verify(userMapper).toResponseDto(testUser);
  }

  @Test
  void findByEmail_WhenUserDoesNotExist_ShouldThrowException() {
    // Given
    String email = "nonexistent@example.com";
    when(findUserByEmail.findByEmail(email)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userController.findByEmail(email))
        .isInstanceOf(UserNotFoundException.class);
    verify(findUserByEmail).findByEmail(email);
    verify(userMapper, never()).toResponseDto(any());
  }

  @Test
  void findByUsername_WhenUserExists_ShouldReturnUser() {
    // Given
    String username = "testuser";
    when(findUserByUsername.findByUsername(username)).thenReturn(Optional.of(testUser));
    when(userMapper.toResponseDto(testUser)).thenReturn(userResponseDto);

    // When
    UserResponseDto result = userController.findByUsername(username);

    // Then
    assertThat(result).isEqualTo(userResponseDto);
    verify(findUserByUsername).findByUsername(username);
    verify(userMapper).toResponseDto(testUser);
  }

  @Test
  void findByUsername_WhenUserDoesNotExist_ShouldThrowException() {
    // Given
    String username = "nonexistentuser";
    when(findUserByUsername.findByUsername(username)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userController.findByUsername(username))
        .isInstanceOf(UserNotFoundException.class);
    verify(findUserByUsername).findByUsername(username);
    verify(userMapper, never()).toResponseDto(any());
  }

  @Test
  void save_WhenUserIsValid_ShouldSaveAndReturnUser() {
    // Given
    User savedUser =
        UserFactory.createUser(testId, testUuid, "Test User", "test@example.com", "testuser");
    when(userMapper.toDomain(userRequestDto)).thenReturn(testUser);
    when(saveUser.save(testUser)).thenReturn(savedUser);
    when(userMapper.toResponseDto(savedUser)).thenReturn(userResponseDto);

    // When
    UserResponseDto result = userController.save(userRequestDto);

    // Then
    assertThat(result).isEqualTo(userResponseDto);
    verify(userMapper).toDomain(userRequestDto);
    verify(saveUser).save(testUser);
    verify(userMapper).toResponseDto(savedUser);
  }

  @Test
  void update_WhenUserExists_ShouldUpdateAndReturnUser() {
    // Given
    User updatedUser = UserFactory.createUser(testId, testUuid, "Updated User",
        "updated@example.com", "updateduser");
    UserResponseDto updatedResponseDto =
        new UserResponseDto(testUuid, "Updated User", "updated@example.com", "updateduser");
    when(userMapper.toDomain(userRequestDto)).thenReturn(testUser);
    when(findUserByUuid.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
    when(updateUser.update(testId, testUser)).thenReturn(updatedUser);
    when(userMapper.toResponseDto(updatedUser)).thenReturn(updatedResponseDto);

    // When
    UserResponseDto result = userController.update(testUuid, userRequestDto);

    // Then
    assertThat(result).isEqualTo(updatedResponseDto);
    verify(userMapper).toDomain(userRequestDto);
    verify(findUserByUuid).findByUuid(testUuid);
    verify(updateUser).update(testId, testUser);
    verify(userMapper).toResponseDto(updatedUser);
  }

  @Test
  void deleteByUuid_ShouldCallDeleteService() {
    // Given
    doNothing().when(deleteUserByUuid).deleteByUuid(testUuid);

    // When
    userController.deleteByUuid(testUuid);

    // Then
    verify(deleteUserByUuid).deleteByUuid(testUuid);
  }
}
