package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindUserByIdTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FindUserById findUserById;

  private User testUser;
  private Integer testId;

  @BeforeEach
  void setUp() {
    testId = 1;
    testUser = UserFactory.createUser(testId, "Test User", "test@example.com", "testuser");
  }

  @Test
  void findById_WhenUserExists_ShouldReturnUser() {
    // Given
    when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));

    // When
    Optional<User> result = findUserById.findById(testId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    assertThat(result.get().getId()).isEqualTo(testId);
    verify(userRepository, times(1)).findById(testId);
  }

  @Test
  void findById_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // Given
    Integer nonExistentId = 999;
    when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When
    Optional<User> result = findUserById.findById(nonExistentId);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findById(nonExistentId);
  }

  @Test
  void findById_WhenIdIsNull_ShouldThrowException() {
    // Given
    when(userRepository.findById(null))
        .thenThrow(new org.springframework.dao.InvalidDataAccessApiUsageException(
            "The given id must not be null"));

    // When & Then
    assertThatThrownBy(() -> findUserById.findById(null))
        .isInstanceOf(org.springframework.dao.InvalidDataAccessApiUsageException.class)
        .hasMessageContaining("The given id must not be null");
    verify(userRepository, times(1)).findById(null);
  }

  @Test
  void findById_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findById(testId))
        .thenThrow(new RuntimeException("Database error"));

    // When & Then
    assertThatThrownBy(() -> findUserById.findById(testId))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findById(testId);
  }

  @Test
  void findById_WhenIdIsZero_ShouldReturnEmpty() {
    // Given
    when(userRepository.findById(0)).thenReturn(Optional.empty());

    // When
    Optional<User> result = findUserById.findById(0);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findById(0);
  }

  @Test
  void findById_WhenIdIsNegative_ShouldReturnEmpty() {
    // Given
    when(userRepository.findById(-1)).thenReturn(Optional.empty());

    // When
    Optional<User> result = findUserById.findById(-1);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findById(-1);
  }
}
