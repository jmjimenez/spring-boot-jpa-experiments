package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindAllUsersTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FindAllUsers findAllUsers;

  private User testUser1;
  private User testUser2;
  private User testUser3;

  @BeforeEach
  void setUp() {
    testUser1 = UserFactory.createUser("Test User 1", "test1@example.com", "testuser1");
    testUser2 = UserFactory.createUser("Test User 2", "test2@example.com", "testuser2");
    testUser3 = UserFactory.createUser("Test User 3", "test3@example.com", "testuser3");
  }

  @Test
  void findAll_WhenUsersExist_ShouldReturnAllUsers() {
    // Given
    List<User> expectedUsers = Arrays.asList(testUser1, testUser2, testUser3);
    when(userRepository.findAll()).thenReturn(expectedUsers);

    // When
    List<User> result = findAllUsers.findAll();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyInAnyOrder(testUser1, testUser2, testUser3);
    assertThat(result.get(0).getName()).isEqualTo("Test User 1");
    assertThat(result.get(0).getEmail()).isEqualTo("test1@example.com");
    assertThat(result.get(0).getUsername()).isEqualTo("testuser1");
    assertThat(result.get(1).getName()).isEqualTo("Test User 2");
    assertThat(result.get(1).getEmail()).isEqualTo("test2@example.com");
    assertThat(result.get(1).getUsername()).isEqualTo("testuser2");
    assertThat(result.get(2).getName()).isEqualTo("Test User 3");
    assertThat(result.get(2).getEmail()).isEqualTo("test3@example.com");
    assertThat(result.get(2).getUsername()).isEqualTo("testuser3");
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void findAll_WhenNoUsersExist_ShouldReturnEmptyList() {
    // Given
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<User> result = findAllUsers.findAll();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void findAll_WhenSingleUserExists_ShouldReturnSingleUser() {
    // Given
    List<User> expectedUsers = Collections.singletonList(testUser1);
    when(userRepository.findAll()).thenReturn(expectedUsers);

    // When
    List<User> result = findAllUsers.findAll();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(testUser1);
    assertThat(result.get(0).getName()).isEqualTo("Test User 1");
    assertThat(result.get(0).getEmail()).isEqualTo("test1@example.com");
    assertThat(result.get(0).getUsername()).isEqualTo("testuser1");
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void findAll_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

    // When & Then
    assertThatThrownBy(() -> findAllUsers.findAll())
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void findAll_WhenRepositoryReturnsNull_ShouldReturnNull() {
    // Given
    when(userRepository.findAll()).thenReturn(null);

    // When
    List<User> result = findAllUsers.findAll();

    // Then
    assertThat(result).isNull();
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void findAll_WhenMultipleCalls_ShouldCallRepositoryEachTime() {
    // Given
    List<User> firstCallUsers = Arrays.asList(testUser1, testUser2);
    List<User> secondCallUsers = Arrays.asList(testUser1, testUser2, testUser3);
    when(userRepository.findAll())
        .thenReturn(firstCallUsers)
        .thenReturn(secondCallUsers);

    // When
    List<User> firstResult = findAllUsers.findAll();
    List<User> secondResult = findAllUsers.findAll();

    // Then
    assertThat(firstResult).hasSize(2);
    assertThat(secondResult).hasSize(3);
    verify(userRepository, times(2)).findAll();
  }

  @Test
  void findAll_WhenUsersHaveSpecialCharacters_ShouldReturnAllUsers() {
    // Given
    User specialUser1 = UserFactory.createUser("José García", "jose@example.com", "jose_garcia");
    User specialUser2 =
        UserFactory.createUser("Maria O'Connor", "maria@example.com", "maria_oconnor");
    List<User> expectedUsers = Arrays.asList(specialUser1, specialUser2);
    when(userRepository.findAll()).thenReturn(expectedUsers);

    // When
    List<User> result = findAllUsers.findAll();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("José García");
    assertThat(result.get(0).getEmail()).isEqualTo("jose@example.com");
    assertThat(result.get(0).getUsername()).isEqualTo("jose_garcia");
    assertThat(result.get(1).getName()).isEqualTo("Maria O'Connor");
    assertThat(result.get(1).getEmail()).isEqualTo("maria@example.com");
    assertThat(result.get(1).getUsername()).isEqualTo("maria_oconnor");
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void findAll_WhenUsersHaveLongNames_ShouldReturnAllUsers() {
    // Given
    User longNameUser = UserFactory.createUser(
        "Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.",
        "dr.john.smith-jones@university.edu",
        "dr_john_smith_jones_iii");
    List<User> expectedUsers = Collections.singletonList(longNameUser);
    when(userRepository.findAll()).thenReturn(expectedUsers);

    // When
    List<User> result = findAllUsers.findAll();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName())
        .isEqualTo("Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.");
    assertThat(result.get(0).getEmail()).isEqualTo("dr.john.smith-jones@university.edu");
    assertThat(result.get(0).getUsername()).isEqualTo("dr_john_smith_jones_iii");
    verify(userRepository, times(1)).findAll();
  }
}
