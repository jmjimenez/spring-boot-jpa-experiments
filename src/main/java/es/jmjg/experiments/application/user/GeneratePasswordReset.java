package es.jmjg.experiments.application.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import es.jmjg.experiments.application.user.dto.PasswordResetDto;
import es.jmjg.experiments.application.user.dto.GeneratePasswordResetDto;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeneratePasswordReset {

  public static final int HOURS_TO_EXPIRE = 24;
  private final UserRepository userRepository;

  public GeneratePasswordReset(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public String generate(GeneratePasswordResetDto dto) {
    User user = userRepository.findByUsername(dto.username())
      .orElseThrow(() -> new UserNotFound("No user found with the provided username and email"));

    if (!user.getEmail().equals(dto.email())) {
      throw new UserNotFound("No user found with the provided username and email");
    }

    return Base64.getEncoder().encodeToString(generateResetkey(dto).getBytes());
  }

  private String generateResetkey(GeneratePasswordResetDto dto) {
    LocalDateTime expiryDate = LocalDateTime.now().plusHours(HOURS_TO_EXPIRE);
    var resetKey = new PasswordResetDto(dto.username(), dto.email(), expiryDate);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new ParameterNamesModule());
    mapper.registerModule(new JavaTimeModule());
    try {
      return mapper.writeValueAsString(resetKey);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
