package es.jmjg.experiments.application.user;

import es.jmjg.experiments.application.user.dto.GeneratePasswordResetDto;
import es.jmjg.experiments.application.user.shared.ResetPasswordKeyService;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeneratePasswordReset {

  private final UserRepository userRepository;
  private final ResetPasswordKeyService resetPasswordKeyService;

  public GeneratePasswordReset(UserRepository userRepository, ResetPasswordKeyService resetPasswordKeyService) {
    this.userRepository = userRepository;
    this.resetPasswordKeyService = resetPasswordKeyService;
  }

  @Transactional(readOnly = true)
  public String generate(GeneratePasswordResetDto dto) {
    User user = userRepository.findByUsername(dto.username())
      .orElseThrow(() -> new UserNotFound("No user found with the provided username and email"));

    if (!user.getEmail().equals(dto.email())) {
      throw new UserNotFound("No user found with the provided username and email");
    }

    return resetPasswordKeyService.generateResetkey(dto.username(), dto.email());
  }
}
