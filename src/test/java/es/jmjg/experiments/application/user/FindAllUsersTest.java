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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.application.user.dto.FindAllUsersDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.shared.UserDetailsFactory;
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
  private Pageable pageable;
  private JwtUserDetails testUserDetails;

  @BeforeEach
  void setUp() {
    testUser1 = UserFactory.createUser("Test User 1", "test1@example.com", "testuser1");
    testUser2 = UserFactory.createUser("Test User 2", "test2@example.com", "testuser2");
    testUser3 = UserFactory.createUser("Test User 3", "test3@example.com", "testuser3");
    pageable = PageRequest.of(0, 10);
    var testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);
  }

  @Test
  void findAll_WhenUsersExist_ShouldReturnAllUsers() {
    // Given
    List<User> expectedUsers = Arrays.asList(testUser1, testUser2, testUser3);
    Page<User> expectedPage = new PageImpl<>(expectedUsers, pageable, expectedUsers.size());
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);

    // When
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getContent()).containsExactlyInAnyOrder(testUser1, testUser2, testUser3);
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
    Page<User> expectedPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);

    // When
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
    List<User> expectedUsers = Collections.singletonList(testUser1);
    Page<User> expectedPage = new PageImpl<>(expectedUsers, pageable, expectedUsers.size());
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);

    // When
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0)).isEqualTo(testUser1);
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
    when(userRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);

    // When & Then
    assertThatThrownBy(() -> findAllUsers.findAll(findAllUsersDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenRepositoryReturnsNull_ShouldReturnNull() {
    // Given
    when(userRepository.findAll(pageable)).thenReturn(null);
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);

    // When
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNull();
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void findAll_WhenMultipleCalls_ShouldCallRepositoryEachTime() {
    // Given
    List<User> firstCallUsers = Arrays.asList(testUser1, testUser2);
    List<User> secondCallUsers = Arrays.asList(testUser1, testUser2, testUser3);
    Page<User> firstPage = new PageImpl<>(firstCallUsers, pageable, firstCallUsers.size());
    Page<User> secondPage = new PageImpl<>(secondCallUsers, pageable, secondCallUsers.size());
    when(userRepository.findAll(pageable))
        .thenReturn(firstPage)
        .thenReturn(secondPage);
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);

    // When
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
    User specialUser1 = UserFactory.createUser("José García", "jose@example.com", "jose_garcia");
    User specialUser2 = UserFactory.createUser("Maria O'Connor", "maria@example.com", "maria_oconnor");
    List<User> expectedUsers = Arrays.asList(specialUser1, specialUser2);
    Page<User> expectedPage = new PageImpl<>(expectedUsers, pageable, expectedUsers.size());
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);

    // When
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
    User longNameUser = UserFactory.createUser(
        "Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.",
        "dr.john.smith-jones@university.edu",
        "dr_john_smith_jones_iii");
    List<User> expectedUsers = Collections.singletonList(longNameUser);
    Page<User> expectedPage = new PageImpl<>(expectedUsers, pageable, expectedUsers.size());
    when(userRepository.findAll(pageable)).thenReturn(expectedPage);
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);

    // When
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
}
