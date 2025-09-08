package es.jmjg.experiments.application.user.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import es.jmjg.experiments.application.user.GeneratePasswordReset;
import es.jmjg.experiments.application.user.dto.GeneratePasswordResetDto;
import es.jmjg.experiments.application.user.dto.PasswordResetDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.infrastructure.repository.UserRepositoryImpl;
import es.jmjg.experiments.shared.BaseIntegration;
import es.jmjg.experiments.shared.TestDataSamples;
import java.time.LocalDateTime;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GeneratePasswordResetIntegrationTest extends BaseIntegration {

  @Autowired
  private GeneratePasswordReset generatePasswordReset;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void whenUserExists_ShouldGeneratePasswordReset() throws JsonProcessingException {
    // Given
    User user = userRepository.findByUuid(TestDataSamples.LEANNE_UUID).orElseThrow();

    // When
    var dto = new GeneratePasswordResetDto(user.getUsername(), user.getEmail());
    var resetKey = generatePasswordReset.generate(dto);

    // Then
    var decodedResetKey = new String(Base64.getDecoder().decode(resetKey));
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new ParameterNamesModule());
    mapper.registerModule(new JavaTimeModule());
    PasswordResetDto resetDto = mapper.readValue(decodedResetKey, PasswordResetDto.class);
    assertThat(resetDto.username()).isEqualTo(user.getUsername());
    assertThat(resetDto.email()).isEqualTo(user.getEmail());
    assertThat(resetDto.expiryDate()).isAfter(LocalDateTime.now());
  }
}
