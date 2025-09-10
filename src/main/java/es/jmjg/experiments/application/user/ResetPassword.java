package es.jmjg.experiments.application.user;

import es.jmjg.experiments.application.user.dto.PasswordResetDto;
import es.jmjg.experiments.application.user.dto.ResetPasswordDto;
import es.jmjg.experiments.application.user.shared.ResetPasswordKeyService;
import es.jmjg.experiments.domain.shared.exception.InvalidRequest;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResetPassword {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ResetPasswordKeyService resetPasswordKeyService;

  public ResetPassword(UserRepository userRepository, PasswordEncoder passwordEncoder, ResetPasswordKeyService resetPasswordKeyService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.resetPasswordKeyService = resetPasswordKeyService;
  }

  @Transactional
  public void reset(ResetPasswordDto dto) {
    PasswordResetDto passwordResetDto = resetPasswordKeyService.parseResetKey(dto.resetKey());

    if (passwordResetDto.expiryDate().isBefore(LocalDateTime.now())) {
      throw new InvalidRequest("The reset key has expired");
    }

    User user = userRepository.findByUsername(dto.username())
      .orElseThrow(() -> new UserNotFound("No user found with the provided username and email"));

    if (!user.getEmail().equals(dto.email())) {
      throw new UserNotFound("No user found with the provided username and email");
    }

    if (!passwordResetDto.email().equals(dto.email())) {
      throw new InvalidRequest("The reset key is not valid");
    }

    if (!passwordResetDto.username().equals(dto.username())) {
      throw new InvalidRequest("The reset key is not valid");
    }

    //TODO: Validate password strength
    user.setPassword(passwordEncoder.encode(dto.newPassword()));
    userRepository.save(user);
  }
}
