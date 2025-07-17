package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.config.TestContainersConfig;
import es.jmjg.experiments.infrastructure.repository.UserRepository;
import es.jmjg.experiments.shared.UserFactory;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FindAllUsersIntegrationTest extends TestContainersConfig {

  @Autowired
  private FindAllUsers findAllUsers;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Environment environment;

  private User testUser1;
  private User testUser2;
  private User testUser3;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    testUser1 = UserFactory.createUser("Test User 1", "test1@example.com", "testuser1");
    testUser2 = UserFactory.createUser("Test User 2", "test2@example.com", "testuser2");
    testUser3 = UserFactory.createUser("Test User 3", "test3@example.com", "testuser3");
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void shouldUseTestProfile() {
    String[] activeProfiles = environment.getActiveProfiles();
    assertThat(activeProfiles).contains("test");
  }

  @Test
  void connectionEstablished() {
    assertThat(TestContainersConfig.getPostgresContainer().isCreated()).isTrue();
    assertThat(TestContainersConfig.getPostgresContainer().isRunning()).isTrue();
  }

  @Test
  void findAll_WhenUsersExist_ShouldReturnAllUsers() {
    // Given
    User savedUser1 = userRepository.save(testUser1);
    User savedUser2 = userRepository.save(testUser2);
    User savedUser3 = userRepository.save(testUser3);

    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getContent()).extracting("name")
        .containsExactlyInAnyOrder("Test User 1", "Test User 2", "Test User 3");
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder("test1@example.com", "test2@example.com", "test3@example.com");
    assertThat(result.getContent()).extracting("username")
        .containsExactlyInAnyOrder("testuser1", "testuser2", "testuser3");
    assertThat(result.getContent()).extracting("id")
        .containsExactlyInAnyOrder(savedUser1.getId(), savedUser2.getId(), savedUser3.getId());
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenNoUsersExist_ShouldReturnEmptyPage() {
    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isEqualTo(0);
    assertThat(result.getTotalPages()).isEqualTo(0);
  }

  @Test
  void findAll_WhenSingleUserExists_ShouldReturnSingleUser() {
    // Given
    User savedUser = userRepository.save(testUser1);

    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo("Test User 1");
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("test1@example.com");
    assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser1");
    assertThat(result.getContent().get(0).getId()).isEqualTo(savedUser.getId());
    assertThat(result.getContent().get(0).getUuid()).isEqualTo(testUser1.getUuid());
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenMultipleUsersWithSameName_ShouldReturnAllUsers() {
    // Given
    User duplicateNameUser = UserFactory.createUser("Test User 1", "duplicate@example.com", "duplicateuser");
    User savedUser1 = userRepository.save(testUser1);
    User savedUser2 = userRepository.save(duplicateNameUser);

    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).extracting("name").allMatch(name -> "Test User 1".equals(name));
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder("test1@example.com", "duplicate@example.com");
    assertThat(result.getContent()).extracting("id")
        .containsExactlyInAnyOrder(savedUser1.getId(), savedUser2.getId());
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenUsersAreUpdated_ShouldReturnUpdatedUsers() {
    // Given
    User savedUser = userRepository.save(testUser1);
    savedUser.setName("Updated Test User");
    savedUser.setEmail("updated@example.com");
    userRepository.save(savedUser);

    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo("Updated Test User");
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("updated@example.com");
    assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser1");
    assertThat(result.getContent().get(0).getId()).isEqualTo(savedUser.getId());
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenUserIsDeleted_ShouldNotReturnDeletedUser() {
    // Given
    User savedUser1 = userRepository.save(testUser1);
    User savedUser2 = userRepository.save(testUser2);
    userRepository.deleteById(savedUser1.getId());

    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo("Test User 2");
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("test2@example.com");
    assertThat(result.getContent().get(0).getId()).isEqualTo(savedUser2.getId());
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenUsersHaveSpecialCharacters_ShouldReturnAllUsers() {
    // Given
    User specialUser1 = UserFactory.createUser("José García", "jose@example.com", "jose_garcia");
    User specialUser2 = UserFactory.createUser("Maria O'Connor", "maria@example.com", "maria_oconnor");
    User savedUser1 = userRepository.save(specialUser1);
    User savedUser2 = userRepository.save(specialUser2);

    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).extracting("name")
        .containsExactlyInAnyOrder("José García", "Maria O'Connor");
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder("jose@example.com", "maria@example.com");
    assertThat(result.getContent()).extracting("username")
        .containsExactlyInAnyOrder("jose_garcia", "maria_oconnor");
    assertThat(result.getContent()).extracting("id")
        .containsExactlyInAnyOrder(savedUser1.getId(), savedUser2.getId());
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenUsersHaveLongNames_ShouldReturnAllUsers() {
    // Given
    User longNameUser = UserFactory.createUser(
        "Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.",
        "dr.john.smith-jones@university.edu",
        "dr_john_smith_jones_iii");
    User savedUser = userRepository.save(longNameUser);

    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName())
        .isEqualTo("Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.");
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("dr.john.smith-jones@university.edu");
    assertThat(result.getContent().get(0).getUsername()).isEqualTo("dr_john_smith_jones_iii");
    assertThat(result.getContent().get(0).getId()).isEqualTo(savedUser.getId());
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenUsersHaveSubdomainEmails_ShouldReturnAllUsers() {
    // Given
    User subdomainUser1 = UserFactory.createUser("Subdomain User 1", "user1@subdomain.example.com", "subdomainuser1");
    User subdomainUser2 = UserFactory.createUser("Subdomain User 2",
        "user2@another-subdomain.example.com", "subdomainuser2");
    User savedUser1 = userRepository.save(subdomainUser1);
    User savedUser2 = userRepository.save(subdomainUser2);

    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).extracting("name")
        .containsExactlyInAnyOrder("Subdomain User 1", "Subdomain User 2");
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder("user1@subdomain.example.com",
            "user2@another-subdomain.example.com");
    assertThat(result.getContent()).extracting("id")
        .containsExactlyInAnyOrder(savedUser1.getId(), savedUser2.getId());
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenMultipleCalls_ShouldReturnConsistentResults() {
    // Given
    User savedUser1 = userRepository.save(testUser1);
    User savedUser2 = userRepository.save(testUser2);

    // When
    Page<User> firstResult = findAllUsers.findAll(pageable);
    Page<User> secondResult = findAllUsers.findAll(pageable);

    // Then
    assertThat(firstResult).isNotNull();
    assertThat(secondResult).isNotNull();
    assertThat(firstResult.getContent()).hasSize(2);
    assertThat(secondResult.getContent()).hasSize(2);
    assertThat(firstResult.getContent()).extracting("id")
        .containsExactlyInAnyOrder(savedUser1.getId(), savedUser2.getId());
    assertThat(secondResult.getContent()).extracting("id")
        .containsExactlyInAnyOrder(savedUser1.getId(), savedUser2.getId());
    assertThat(firstResult.getTotalElements()).isEqualTo(2);
    assertThat(secondResult.getTotalElements()).isEqualTo(2);
    assertThat(firstResult.getTotalPages()).isEqualTo(1);
    assertThat(secondResult.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenUsersHaveDifferentDataTypes_ShouldReturnAllUsers() {
    // Given
    User userWithNumbers = UserFactory.createUser("User123", "user123@example.com", "user123");
    User userWithUnderscores = UserFactory.createUser("User_With_Underscores",
        "user_with_underscores@example.com", "user_with_underscores");
    User userWithDashes = UserFactory.createUser("User-With-Dashes", "user-with-dashes@example.com",
        "user-with-dashes");

    User savedUser1 = userRepository.save(userWithNumbers);
    User savedUser2 = userRepository.save(userWithUnderscores);
    User savedUser3 = userRepository.save(userWithDashes);

    // When
    Page<User> result = findAllUsers.findAll(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getContent()).extracting("name")
        .containsExactlyInAnyOrder("User123", "User_With_Underscores", "User-With-Dashes");
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder("user123@example.com", "user_with_underscores@example.com",
            "user-with-dashes@example.com");
    assertThat(result.getContent()).extracting("id")
        .containsExactlyInAnyOrder(savedUser1.getId(), savedUser2.getId(), savedUser3.getId());
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }
}
