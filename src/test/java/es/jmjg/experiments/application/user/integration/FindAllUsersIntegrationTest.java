package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.user.FindAllUsers;
import es.jmjg.experiments.application.user.dto.FindAllUsersDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
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

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void findAll_WhenUsersExist_ShouldReturnAllUsers() {
    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
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
    userRepository.deleteAll();

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
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
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
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

  private void deleteAllUsers() {
    userRepository.deleteAll();
    entityManager.flush();
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
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
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
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
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
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(5);
    assertThat(result.getContent()).extracting("name")
        .doesNotContain("Leanne Graham");
    assertThat(result.getContent()).extracting("uuid")
        .doesNotContain(TestDataSamples.LEANNE_UUID);
    assertThat(result.getTotalElements()).isEqualTo(5);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  @Transactional
  void findAll_WhenUsersHaveSpecialCharacters_ShouldReturnAllUsers() {
    // Given - clear all users and add users with special characters
    deleteAllUsers();
    User specialUser1 = UserFactory.createUser(TestDataSamples.LEANNE_UUID, "José García",
        "jose@example.com", "jose_garcia");
    User specialUser2 = UserFactory.createUser(TestDataSamples.ERVIN_UUID, "Maria O'Connor",
        "maria@example.com", "maria_oconnor");
    userRepository.save(specialUser1);
    userRepository.save(specialUser2);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).extracting("name")
        .containsExactlyInAnyOrder("José García", "Maria O'Connor");
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder("jose@example.com", "maria@example.com");
    assertThat(result.getContent()).extracting("username")
        .containsExactlyInAnyOrder("jose_garcia", "maria_oconnor");
    assertThat(result.getContent()).extracting("uuid")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_UUID, TestDataSamples.ERVIN_UUID);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  @Transactional
  void findAll_WhenUsersHaveLongNames_ShouldReturnAllUsers() {
    // Given - clear all users and add user with long name
    deleteAllUsers();
    User longNameUser = UserFactory.createUser(TestDataSamples.LEANNE_UUID,
        "Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.",
        "dr.john.smith-jones@university.edu",
        "dr_john_smith_jones_iii");
    userRepository.save(longNameUser);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getName())
        .isEqualTo("Dr. John Michael Smith-Jones III, Ph.D., M.D., F.A.C.S.");
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("dr.john.smith-jones@university.edu");
    assertThat(result.getContent().get(0).getUsername()).isEqualTo("dr_john_smith_jones_iii");
    assertThat(result.getContent().get(0).getUuid()).isEqualTo(TestDataSamples.LEANNE_UUID);
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  @Transactional
  void findAll_WhenUsersHaveSubdomainEmails_ShouldReturnAllUsers() {
    // Given - clear all users and add users with subdomain emails
    deleteAllUsers();
    User subdomainUser1 = UserFactory.createUser(TestDataSamples.LEANNE_UUID, "Subdomain User 1",
        "user1@subdomain.example.com", "subdomainuser1");
    User subdomainUser2 = UserFactory.createUser(TestDataSamples.ERVIN_UUID, "Subdomain User 2",
        "user2@another-subdomain.example.com", "subdomainuser2");
    userRepository.save(subdomainUser1);
    userRepository.save(subdomainUser2);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).extracting("name")
        .containsExactlyInAnyOrder("Subdomain User 1", "Subdomain User 2");
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder("user1@subdomain.example.com",
            "user2@another-subdomain.example.com");
    assertThat(result.getContent()).extracting("uuid")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_UUID, TestDataSamples.ERVIN_UUID);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  void findAll_WhenMultipleCalls_ShouldReturnConsistentResults() {
    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
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
    // Given - clear all users and add users with different data types
    deleteAllUsers();
    User userWithNumbers = UserFactory.createUser(TestDataSamples.LEANNE_UUID, "User123",
        "user123@example.com", "user123");
    User userWithUnderscores = UserFactory.createUser(TestDataSamples.ERVIN_UUID, "User_With_Underscores",
        "user_with_underscores@example.com", "user_with_underscores");
    User userWithDashes = UserFactory.createUser(TestDataSamples.ADMIN_UUID, "User-With-Dashes",
        "user-with-dashes@example.com", "user-with-dashes");

    userRepository.save(userWithNumbers);
    userRepository.save(userWithUnderscores);
    userRepository.save(userWithDashes);

    // When
    FindAllUsersDto findAllUsersDto = new FindAllUsersDto(pageable);
    Page<User> result = findAllUsers.findAll(findAllUsersDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getContent()).extracting("name")
        .containsExactlyInAnyOrder("User123", "User_With_Underscores", "User-With-Dashes");
    assertThat(result.getContent()).extracting("email")
        .containsExactlyInAnyOrder("user123@example.com", "user_with_underscores@example.com",
            "user-with-dashes@example.com");
    assertThat(result.getContent()).extracting("uuid")
        .containsExactlyInAnyOrder(TestDataSamples.LEANNE_UUID, TestDataSamples.ERVIN_UUID,
            TestDataSamples.ADMIN_UUID);
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }
}
