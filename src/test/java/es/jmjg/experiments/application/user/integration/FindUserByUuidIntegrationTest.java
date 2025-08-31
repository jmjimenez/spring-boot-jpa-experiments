package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.application.user.FindUserByUuid;
import es.jmjg.experiments.application.user.dto.FindUserByUuidDto;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserDetailsFactory;
import es.jmjg.experiments.shared.UserFactory;

class FindUserByUuidIntegrationTest extends BaseIntegration {

  @Autowired
  private FindUserByUuid findUserByUuid;

  @Autowired
  private UserRepositoryImpl userRepository;

  private JwtUserDetails testUserDetails;
  private JwtUserDetails adminUserDetails;

  @BeforeEach
  void setUp() {
    User testUser = UserFactory.createUser("Test User", "test@example.com", "testuser");
    testUserDetails = UserDetailsFactory.createJwtUserDetails(testUser);
    User adminUser = UserFactory.createUser(TestDataSamples.ADMIN_NAME,
        TestDataSamples.ADMIN_EMAIL, TestDataSamples.ADMIN_USERNAME);
    adminUserDetails = UserDetailsFactory.createJwtUserDetails(adminUser);
  }

  @Test
  void findByUuid_WhenUserExists_ShouldReturnUser() {
    // Given
    UUID testUuid = UUID.randomUUID();
    User testUser = UserFactory.createUser(testUuid, "Test User", "test@example.com", "testuser");
    User savedUser = userRepository.save(testUser);

    // When
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(testUuid, adminUserDetails);
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
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(UUID.randomUUID(), adminUserDetails);
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
    FindUserByUuidDto findUserByUuidDto1 = new FindUserByUuidDto(secondUuid, adminUserDetails);
    FindUserByUuidDto findUserByUuidDto2 = new FindUserByUuidDto(thirdUuid, adminUserDetails);
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
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(fourthUuid, adminUserDetails);
    Optional<User> result = findUserByUuid.findByUuid(findUserByUuidDto);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo(fourthUser.getName());
    assertThat(result.get().getEmail()).isEqualTo(fourthUser.getEmail());
    assertThat(result.get().getUsername()).isEqualTo(fourthUser.getUsername());
    assertThat(result.get().getUuid()).isEqualTo(fourthUuid);
  }

  @Test
  void findByUuid_WhenNonAdminUserTriesToAccessOtherUserData_ShouldThrowForbidden() {
    // Given
    FindUserByUuidDto findUserByUuidDto = new FindUserByUuidDto(TestDataSamples.LEANNE_UUID, testUserDetails);

    // When & Then
    assertThatThrownBy(() -> findUserByUuid.findByUuid(findUserByUuidDto))
        .isInstanceOf(Forbidden.class)
        .hasMessage("Access denied: only admins or the user themselves can view user data");
  }
}
