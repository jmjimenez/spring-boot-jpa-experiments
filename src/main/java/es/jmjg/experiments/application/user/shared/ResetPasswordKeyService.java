package es.jmjg.experiments.application.user.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import es.jmjg.experiments.application.user.dto.PasswordResetDto;
import es.jmjg.experiments.domain.shared.exception.InvalidRequest;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordKeyService {

  public static final int HOURS_TO_EXPIRE = 24;

  private final ObjectMapper mapper;

  public ResetPasswordKeyService() {
    this.mapper = new ObjectMapper();
    this.mapper.registerModule(new ParameterNamesModule());
    this.mapper.registerModule(new JavaTimeModule());
  }

  public String generateResetkey(@NotNull String username, @NotNull String email) {
    LocalDateTime expiryDate = LocalDateTime.now().plusHours(HOURS_TO_EXPIRE);
    var passwordResetDto = new PasswordResetDto(username, email, expiryDate);
    try {
      return Base64.getEncoder().encodeToString(mapper.writeValueAsString(passwordResetDto).getBytes());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public PasswordResetDto parseResetKey(String resetKey) {
    try {
      return mapper.readValue(new String(Base64.getDecoder().decode(resetKey)), PasswordResetDto.class);

    } catch (JsonProcessingException e) {
      throw new InvalidRequest("The reset key is not valid");
    }
  }
}
