package es.jmjg.experiments.infrastructure.controller;

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

import es.jmjg.experiments.infrastructure.controller.dto.AuthenticationRequest;
import es.jmjg.experiments.infrastructure.controller.dto.AuthenticationResponse;
import es.jmjg.experiments.infrastructure.security.JwtTokenService;
import es.jmjg.experiments.infrastructure.security.JwtUserDetailsService;
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
  private final JwtTokenService jwtTokenService;

  @PostMapping("/authenticate")
  @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token. Use this endpoint to get a token for accessing protected API endpoints.", security = {})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")
  })
  public AuthenticationResponse authenticate(
      @Parameter(description = "User credentials", required = true) @RequestBody @Valid final AuthenticationRequest authenticationRequest) {
    return authenticateUser(authenticationRequest.getLogin(), authenticationRequest.getPassword());
  }

  @PostMapping(value = "/authenticate", consumes = "application/x-www-form-urlencoded")
  @Operation(summary = "Authenticate user (OAuth2)", description = "OAuth2 password flow endpoint for authentication", security = {})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")
  })
  public AuthenticationResponse authenticateOAuth2(
      @Parameter(description = "Username for authentication", required = true) @RequestParam("username") String username,
      @Parameter(description = "Password for authentication", required = true) @RequestParam("password") String password) {
    return authenticateUser(username, password);
  }

  private AuthenticationResponse authenticateUser(String username, String password) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
    } catch (final BadCredentialsException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
    final AuthenticationResponse authenticationResponse = new AuthenticationResponse();
    authenticationResponse.setAccessToken(jwtTokenService.generateToken(userDetails));
    return authenticationResponse;
  }
}
