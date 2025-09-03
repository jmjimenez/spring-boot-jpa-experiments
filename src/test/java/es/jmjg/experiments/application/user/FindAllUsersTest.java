package es.jmjg.experiments.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.dto.FindAllUsersDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.shared.AuthenticatedUserFactory;
import es.jmjg.experiments.shared.UserFactory;

@ExtendWith(MockitoExtension.class)
class FindAllUsersTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FindAllUsers findAllUsers;

  private User foundUser1;
  private User foundUser2;
  private User foundUser3;

  private AuthenticatedUserDto authenticatedTestUser;
  private AuthenticatedUserDto authenticatedAdminUser;

  @BeforeEach
  void setUp() {
    foundUser1 = UserFactory.createUser("Test User 1", "test1@example.com", "testuser1");
    foundUser2 = UserFactory.createUser("Test User 2", "test2@example.com", "testuser2");
    foundUser3 = UserFactory.createUser("Test User 3", "test3@example.com", "testuser3");

    var testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    authenticatedTestUser = AuthenticatedUserFactory.createAuthenticatedUserDto(testUser);
    var adminUser = UserFactory.createUser("Admin User", "admin@example.com", "admin");
    authenticatedAdminUser = AuthenticatedUserFactory.createAuthenticatedUserDto(adminUser);
  }

  @Test
  void findAll_WhenUsersExist_ShouldReturnAllUsers() {
    // Given
    List<User> expectedUsers = Arrays.asList(foundUser1, foundUser2, foundUser3);
    var pageable = PageRequest.of(0, 10);
    Page<User> expectedPage = new PageImpl<>(expectedUsers, pageable, expectedUsers.size());
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, authenticatedAdminUser);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getContent()).containsExactlyInAnyOrder(foundUser1, foundUser2, foundUser3);
    assertThat(result.getContent().get(0).getName()).isEqualTo("Test User 1");
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("test1@example.com");
    assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser1");
    assertThat(result.getContent().get(1).getName()).isEqualTo("Test User 2");
    assertThat(result.getContent().get(1).getEmail()).isEqualTo("test2@example.com");
    assertThat(result.getContent().get(1).getUsername()).isEqualTo("testuser2");
    assertThat(result.getContent().get(2).getName()).isEqualTo("Test User 3");
    assertThat(result.getContent().get(2).getEmail()).isEqualTo("test3@example.com");
    assertThat(result.getContent().get(2).getUsername()).isEqualTo("testuser3");
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getTotalPages()).isEqualTo(1);
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenNoUsersExist_ShouldReturnEmptyPage() {
    // Given
    var pageable = PageRequest.of(0, 10);
    Page<User> expectedPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, authenticatedAdminUser);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isEqualTo(0);
    assertThat(result.getTotalPages()).isEqualTo(0);
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenSingleUserExists_ShouldReturnSingleUser() {
    // Given
    var pageable = PageRequest.of(0, 10);
    List<User> expectedUsers = Collections.singletonList(foundUser1);
    Page<User> expectedPage = new PageImpl<>(expectedUsers, pageable, expectedUsers.size());
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, authenticatedAdminUser);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0)).isEqualTo(foundUser1);
    assertThat(result.getContent().get(0).getName()).isEqualTo("Test User 1");
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("test1@example.com");
    assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser1");
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(1);
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    var pageable = PageRequest.of(0, 10);
    when(userRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

    // When & Then
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, authenticatedAdminUser);
    assertThatThrownBy(() -> findAllUsers.findAll(findAllUsersDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenRepositoryReturnsNull_ShouldReturnNull() {
    // Given
    var pageable = PageRequest.of(0, 10);
    when(userRepository.findAll(pageable)).thenReturn(null);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, authenticatedAdminUser);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNull();
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenMultipleCalls_ShouldCallRepositoryEachTime() {
    // Given
    var pageable = PageRequest.of(0, 10);
    List<User> firstCallUsers = Arrays.asList(foundUser1, foundUser2);
    List<User> secondCallUsers = Arrays.asList(foundUser1, foundUser2, foundUser3);
    Page<User> firstPage = new PageImpl<>(firstCallUsers, pageable, firstCallUsers.size());
    Page<User> secondPage = new PageImpl<>(secondCallUsers, pageable, secondCallUsers.size());
    when(userRepository.findAll(pageable))
        .thenReturn(firstPage)
        .thenReturn(secondPage);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, authenticatedAdminUser);
    Page<User> firstResult = findAllUsers.findAll(findAllUsersDto);
    Page<User> secondResult = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(firstResult.getContent()).hasSize(2);
    assertThat(secondResult.getContent()).hasSize(3);
    verify(userRepository, times(2)).findAll(pageable);
  }

  @Test
  void findAll_WhenUsersHaveSpecialCharacters_ShouldReturnAllUsers() {
    // Given
    var pageable = PageRequest.of(0, 10);
    User specialUser1 = UserFactory.createUser("José García", "jose@example.com", "jose_garcia");
    User specialUser2 = UserFactory.createUser("Maria O'Connor", "maria@example.com", "maria_oconnor");
    List<User> expectedUsers = Arrays.asList(specialUser1, specialUser2);
    Page<User> expectedPage = new PageImpl<>(expectedUsers, pageable, expectedUsers.size());
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, authenticatedAdminUser);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getName()).isEqualTo("José García");
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("jose@example.com");
    assertThat(result.getContent().get(0).getUsername()).isEqualTo("jose_garcia");
    assertThat(result.getContent().get(1).getName()).isEqualTo("Maria O'Connor");
    assertThat(result.getContent().get(1).getEmail()).isEqualTo("maria@example.com");
    assertThat(result.getContent().get(1).getUsername()).isEqualTo("maria_oconnor");
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(1);
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenUsersHaveLongNames_ShouldReturnAllUsers() {
    // Given
    var pageable = PageRequest.of(0, 10);
    User longNameUser = UserFactory.createUser(
        "Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.",
        "dr.john.smith-jones@university.edu",
        "dr_john_smith_jones_iii");
    List<User> expectedUsers = Collections.singletonList(longNameUser);
    Page<User> expectedPage = new PageImpl<>(expectedUsers, pageable, expectedUsers.size());
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, authenticatedAdminUser);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName())
        .isEqualTo("Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.");
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("dr.john.smith-jones@university.edu");
    assertThat(result.getContent().get(0).getUsername()).isEqualTo("dr_john_smith_jones_iii");
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(1);
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenAuthenticatedUserIsTestUser_ShouldThrowForbiddenException() {
    // Given
    var pageable = PageRequest.of(0, 10);
    // When & Then
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, authenticatedTestUser);
    assertThatThrownBy(() -> findAllUsers.findAll(findAllUsersDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Only admin users can view all users");
    verify(userRepository, never()).findAll(any(Pageable.class));
  }
}
