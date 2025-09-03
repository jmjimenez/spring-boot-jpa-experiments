package es.jmjg.experiments.infrastructure.controller.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.jmjg.experiments.infrastructure.config.security.JwtTokenService;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetailsService;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationRequestDto;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationResponseDto;
import es.jmjg.experiments.infrastructure.controller.authentication.mapper.AuthenticationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication operations")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final JwtUserDetailsService jwtUserDetailsService;
  private final AuthenticationMapper authenticationMapper;

  @PostMapping("/authenticate")
  @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token. Use this endpoint to get a token for accessing protected API endpoints.", security = {})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")
  })
  public AuthenticationResponseDto authenticate(
      @Parameter(description = "User credentials", required = true) @RequestBody @Valid final AuthenticationRequestDto authenticationRequest) {
    authenticateUser(authenticationRequest.getLogin(), authenticationRequest.getPassword());

    final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getLogin());
    return authenticationMapper.toAuthenticationResponseDto(userDetails);
  }

  @PostMapping(value = "/authenticate", consumes = "application/x-www-form-urlencoded")
  @Operation(summary = "Authenticate user (OAuth2)", description = "OAuth2 password flow endpoint for authentication", security = {})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")
  })
  public AuthenticationResponseDto authenticateOAuth2(
      @Parameter(description = "Username for authentication", required = true) @RequestParam("username") String username,
      @Parameter(description = "Password for authentication", required = true) @RequestParam("password") String password) {
    authenticateUser(username, password);

    final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
    return authenticationMapper.toAuthenticationResponseDto(userDetails);
  }

  private void authenticateUser(String username, String password) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
    } catch (final BadCredentialsException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }
}
