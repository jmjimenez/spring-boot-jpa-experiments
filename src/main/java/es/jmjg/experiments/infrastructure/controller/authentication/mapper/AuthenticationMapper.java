package es.jmjg.experiments.infrastructure.controller.authentication.mapper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import es.jmjg.experiments.infrastructure.config.security.JwtTokenService;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationResponseDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationMapper {

  private final JwtTokenService jwtTokenService;

  public AuthenticationResponseDto toAuthenticationResponseDto(UserDetails userDetails) {
    final AuthenticationResponseDto authenticationResponse = new AuthenticationResponseDto();
    authenticationResponse.setAccessToken(jwtTokenService.generateToken(userDetails));
    return authenticationResponse;
  }
}
