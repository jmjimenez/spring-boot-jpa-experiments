package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class FindUserByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserByUuid findUserByUuid;

  @Autowired
  private UserRepositoryImpl userRepository;

  private JwtUserDetails testUserDetails;

  @BeforeEach
  void setUp() {
    User testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createUserUserDetails(testUser);
  }

  @Test
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // Given
    UUID testUuid = UUID.randomUUID();
    User testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    User savedUser = userRepository.save(testUser);

    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUuid, testUserDetails);
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    assertThat(result.get().getUuid()).isEqualTo(testUuid);
    assertThat(result.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void findByUuid_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(UUID.randomUUID(), testUserDetails);
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuid_WhenUuidIsNull_ShouldReturnEmpty() {
    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(null, testUserDetails);
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void findByUuid_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
    // Given
    UUID secondUuid = UUID.randomUUID();
    User secondUser = UserFactory.createUser(secondUuid, "Second User", "second@example.com", "seconduser");

    UUID thirdUuid = UUID.randomUUID();
    User thirdUser = UserFactory.createUser(thirdUuid, "Third User", "third@example.com", "thirduser");

    User savedSecondUser = userRepository.save(secondUser);
    User savedThirdUser = userRepository.save(thirdUser);

    // When
    FindUserByUuidDto findUserByUuidDto1 = new FindUserByUuidDto(secondUuid, testUserDetails);
    FindUserByUuidDto findUserByUuidDto2 = new FindUserByUuidDto(thirdUuid, testUserDetails);
    Optional<User> firstResult = findUserByUuid.findByUuid(findUserByUuidDto1);
    Optional<User> secondResult = findUserByUuid.findByUuid(findUserByUuidDto2);

    // Then
    assertThat(firstResult).isPresent();
    assertThat(firstResult.get().getName()).isEqualTo(secondUser.getName());
    assertThat(firstResult.get().getId()).isEqualTo(savedSecondUser.getId());

    assertThat(secondResult).isPresent();
    assertThat(secondResult.get().getName()).isEqualTo(thirdUser.getName());
    assertThat(secondResult.get().getId()).isEqualTo(savedThirdUser.getId());
  }

  @Test
  void findByUuid_WhenUserIsUpdated_ShouldReturnUpdatedUser() {
    // Given
    UUID fourthUuid = UUID.randomUUID();
    User fourthUser = UserFactory.createUser(fourthUuid, "Fourth User", "fourth@example.com", "fourthuser");
    User savedUser = userRepository.save(fourthUser);

    savedUser.setName("Updated Test User");
    savedUser.setEmail("updated@example.com");
    userRepository.save(savedUser);

    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(fourthUuid, testUserDetails);
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(fourthUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(fourthUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(fourthUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(fourthUuid);
  }
}
