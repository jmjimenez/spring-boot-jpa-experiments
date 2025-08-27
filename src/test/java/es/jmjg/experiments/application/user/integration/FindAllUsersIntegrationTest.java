package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.dto.FindAllUsersDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class FindAllUsersIntegrationTest extends BaseIntegration {
  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private FindAllUsers findAllUsers;

  @Autowired
  private UserRepositoryImpl userRepository;

  private Pageable pageable;
  private JwtUserDetails testUserDetails;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(0, 10);
    User testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);
  }

  @Test
  void findAll_WhenUsersExist_ShouldReturnAllUsers() {
    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(6);
    assertThat(result.getContent()).extracting("name")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_NAME, TestDataSamples.ERVIN_NAME,
            TestDataSamples.CLEMENTINE_NAME,
            TestDataSamples.PATRICIA_NAME, TestDataSamples.CHELSEY_NAME, TestDataSamples.ADMIN_NAME);
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_EMAIL, TestDataSamples.ERVIN_EMAIL,
            TestDataSamples.CLEMENTINE_EMAIL, TestDataSamples.PATRICIA_EMAIL,
            TestDataSamples.CHELSEY_EMAIL, TestDataSamples.ADMIN_EMAIL);
    assertThat(result.getContent()).extracting("username")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_USERNAME, TestDataSamples.ERVIN_USERNAME,
            TestDataSamples.CLEMENTINE_USERNAME, TestDataSamples.PATRICIA_USERNAME,
            TestDataSamples.CHELSEY_USERNAME, TestDataSamples.ADMIN_USERNAME);
    assertThat(result.getContent()).extracting("uuid")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_UUID, TestDataSamples.ERVIN_UUID,
            TestDataSamples.CLEMENTINE_UUID,
            TestDataSamples.PATRICIA_UUID, TestDataSamples.CHELSEY_UUID, TestDataSamples.ADMIN_UUID);
    assertThat(result.getTotalElements()).isEqualTo(6);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  @Transactional
  void findAll_WhenNoUsersExist_ShouldReturnEmptyPage() {
    // Given - clear all users
    deleteAllUsers();

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isEqualTo(0);
    assertThat(result.getTotalPages()).isEqualTo(0);
  }

  @Test
  @Transactional
  void findAll_WhenSingleUserExists_ShouldReturnSingleUser() {
    // Given - clear all users and add one
    deleteAllUsers();
    // userRepository.count();
    User singleUser = UserFactory.createUser(TestDataSamples.LEANNE_UUID, TestDataSamples.LEANNE_NAME,
        TestDataSamples.LEANNE_EMAIL, TestDataSamples.LEANNE_USERNAME);
    userRepository.save(singleUser);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo(TestDataSamples.LEANNE_NAME);
    assertThat(result.getContent().get(0).getEmail()).isEqualTo(TestDataSamples.LEANNE_EMAIL);
    assertThat(result.getContent().get(0).getUsername()).isEqualTo(TestDataSamples.LEANNE_USERNAME);
    assertThat(result.getContent().get(0).getUuid()).isEqualTo(TestDataSamples.LEANNE_UUID);
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  @Transactional
  void findAll_WhenMultipleUsersWithSameName_ShouldReturnAllUsers() {
    // Given - clear all users and add users with same name
    deleteAllUsers();
    User user1 = UserFactory.createUser(TestDataSamples.LEANNE_UUID, TestDataSamples.LEANNE_NAME,
        TestDataSamples.LEANNE_EMAIL, TestDataSamples.LEANNE_USERNAME);
    User user2 = UserFactory.createUser(TestDataSamples.ERVIN_UUID, TestDataSamples.LEANNE_NAME,
        TestDataSamples.ERVIN_EMAIL, TestDataSamples.ERVIN_USERNAME);
    userRepository.save(user1);
    userRepository.save(user2);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).extracting("name").allMatch(name -> TestDataSamples.LEANNE_NAME.equals(name));
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_EMAIL, TestDataSamples.ERVIN_EMAIL);
    assertThat(result.getContent()).extracting("uuid")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_UUID, TestDataSamples.ERVIN_UUID);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  @Transactional
  void findAll_WhenUsersAreUpdated_ShouldReturnUpdatedUsers() {
    // Given - find existing user and update it
    User existingUser = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    existingUser.setName("Updated Leanne Graham");
    existingUser.setEmail("updated.leanne@example.com");
    userRepository.save(existingUser);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(6);
    assertThat(result.getContent()).extracting("name")
        .contains("Updated Leanne Graham");
    assertThat(result.getContent()).extracting("email")
        .contains("updated.leanne@example.com");
    assertThat(result.getTotalElements()).isEqualTo(6);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  @Transactional
  void findAll_WhenUserIsDeleted_ShouldNotReturnDeletedUser() {
    // Given - delete one user from the existing data
    User userToDelete = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();
    userRepository.deleteById(userToDelete.getId());

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(5);
    assertThat(result.getContent()).extracting("name")
        .doesNotContain(TestDataSamples.LEANNE_NAME);
    assertThat(result.getContent()).extracting("uuid")
        .doesNotContain(TestDataSamples.LEANNE_UUID);
    assertThat(result.getTotalElements()).isEqualTo(5);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @ParameterizedTest
  @MethodSource("provideUsersWithSpecialCharacters")
  @Transactional
  void findAll_WhenUsersHaveSpecialCharacters_ShouldReturnAllUsers(UUID uuid, String name, String email,
      String username) {
    // Given - clear all users and add one user with special characters
    deleteAllUsers();
    User specialUser = UserFactory.createUser(uuid, name, email, username);
    userRepository.save(specialUser);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo(name);
    assertThat(result.getContent().get(0).getEmail()).isEqualTo(email);
    assertThat(result.getContent().get(0).getUsername()).isEqualTo(username);
    assertThat(result.getContent().get(0).getUuid()).isEqualTo(uuid);
  }

  private static Stream<Arguments> provideUsersWithSpecialCharacters() {
    return Stream.of(
        Arguments.of(UUID.randomUUID(), "José García", "jose@example.com", "jose_garcia"),
        Arguments.of(UUID.randomUUID(), "Maria O'Connor", "maria@example.com", "maria_oconnor"));
  }

  @ParameterizedTest
  @MethodSource("provideUsersWithLongNames")
  @Transactional
  void findAll_WhenUsersHaveLongNames_ShouldReturnAllUsers(UUID uuid, String name, String email,
      String username) {
    // Given - clear all users and add one user with long name
    deleteAllUsers();
    User longNameUser = UserFactory.createUser(uuid, name, email, username);
    userRepository.save(longNameUser);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo(name);
    assertThat(result.getContent().get(0).getEmail()).isEqualTo(email);
    assertThat(result.getContent().get(0).getUsername()).isEqualTo(username);
    assertThat(result.getContent().get(0).getUuid()).isEqualTo(uuid);
  }

  private static Stream<Arguments> provideUsersWithLongNames() {
    return Stream.of(
        Arguments.of(UUID.randomUUID(), "Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.",
            "dr.john.smith-jones@university.edu", "dr_john_smith_jones_iii"),
        Arguments.of(UUID.randomUUID(),
            "Professor María del Carmen Rodríguez-González y Fernández de la Vega",
            "prof.maria.rodriguez-gonzalez@universidad.es", "prof_maria_rodriguez_gonzalez"),
        Arguments.of(UUID.randomUUID(),
            "Sir William Henry Gates III, KBE, FRS, FRSE, HonFRCP, HonFREng",
            "sir.william.gates@microsoft.com", "sir_william_gates_iii"));
  }

  @ParameterizedTest
  @MethodSource("provideUsersWithSubdomainEmails")
  @Transactional
  void findAll_WhenUsersHaveSubdomainEmails_ShouldReturnAllUsers(UUID uuid, String name, String email,
      String username) {
    // Given - clear all users and add one user with subdomain email
    deleteAllUsers();
    User subdomainUser = UserFactory.createUser(uuid, name, email, username);
    userRepository.save(subdomainUser);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo(name);
    assertThat(result.getContent().get(0).getEmail()).isEqualTo(email);
    assertThat(result.getContent().get(0).getUsername()).isEqualTo(username);
    assertThat(result.getContent().get(0).getUuid()).isEqualTo(uuid);
  }

  private static Stream<Arguments> provideUsersWithSubdomainEmails() {
    return Stream.of(
        Arguments.of(UUID.randomUUID(), "Subdomain User 1", "user1@subdomain.example.com", "subdomainuser1"),
        Arguments.of(UUID.randomUUID(), "Subdomain User 2", "user2@another-subdomain.example.com", "subdomainuser2"),
        Arguments.of(UUID.randomUUID(), "Deep Subdomain User", "user@deep.nested.subdomain.example.com",
            "deepsubdomainuser"),
        Arguments.of(UUID.randomUUID(), "Multi-Level User", "admin@dev.staging.production.company.org",
            "multileveluser"));
  }

  @Test
  void findAll_WhenMultipleCalls_ShouldReturnConsistentResults() {
    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> firstResult = findAllUsers.findAll(findAllUsersDto);
    Page<User> secondResult = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(firstResult).isNotNull();
    assertThat(secondResult).isNotNull();
    assertThat(firstResult.getContent()).hasSize(6);
    assertThat(secondResult.getContent()).hasSize(6);
    assertThat(firstResult.getContent()).extracting("uuid")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_UUID, TestDataSamples.ERVIN_UUID,
            TestDataSamples.CLEMENTINE_UUID, TestDataSamples.PATRICIA_UUID,
            TestDataSamples.CHELSEY_UUID, TestDataSamples.ADMIN_UUID);
    assertThat(secondResult.getContent()).extracting("uuid")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_UUID, TestDataSamples.ERVIN_UUID,
            TestDataSamples.CLEMENTINE_UUID, TestDataSamples.PATRICIA_UUID,
            TestDataSamples.CHELSEY_UUID, TestDataSamples.ADMIN_UUID);
    assertThat(firstResult.getTotalElements()).isEqualTo(6);
    assertThat(secondResult.getTotalElements()).isEqualTo(6);
    assertThat(firstResult.getTotalPages()).isEqualTo(1);
    assertThat(secondResult.getTotalPages()).isEqualTo(1);
  }

  @Test
  @Transactional
  void findAll_WhenUsersHaveDifferentDataTypes_ShouldReturnAllUsers() {
    // Given - add users with different data types
    UUID uuid1 = UUID.randomUUID();
    UUID uuid2 = UUID.randomUUID();
    UUID uuid3 = UUID.randomUUID();
    User userWithNumbers = UserFactory.createUser(uuid1, "User123",
        "user123@example.com", "user123");
    User userWithUnderscores = UserFactory.createUser(uuid2, "User_With_Underscores",
        "user_with_underscores@example.com", "user_with_underscores");
    User userWithDashes = UserFactory.createUser(uuid3, "User-With-Dashes",
        "user-with-dashes@example.com", "user-with-dashes");

    userRepository.save(userWithNumbers);
    userRepository.save(userWithUnderscores);
    userRepository.save(userWithDashes);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable, testUserDetails);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).extracting("name")
        .contains("User123", "User_With_Underscores", "User-With-Dashes");
    assertThat(result.getContent()).extracting("email")
        .contains("user123@example.com", "user_with_underscores@example.com",
            "user-with-dashes@example.com");
    assertThat(result.getContent()).extracting("uuid")
        .contains(uuid1, uuid2, uuid3);
  }

  private void deleteAllUsers() {
    userRepository.deleteAll();
    entityManager.flush();
  }
}
