package es.jmjg.experiments.infrastructure.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.ControllerTestConfig;
import es.jmjg.experiments.infrastructure.controller.dto.AuthenticationRequest;
import es.jmjg.experiments.infrastructure.security.JwtTokenService;
import es.jmjg.experiments.infrastructure.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.security.JwtUserDetailsService;
import es.jmjg.experiments.shared.UserFactory;

@WebMvcTest(AuthenticationController.class)
@Import(ControllerTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthenticationControllerTest {

  private static final String AUTHENTICATE_ENDPOINT = "/authenticate";
  private static final String TEST_USERNAME = "admin";
  private static final String TEST_PASSWORD = "testpass";
  private static final String TEST_PASSWORD_HASH = "$0a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtUserDetailsService jwtUserDetailsService;

  @Autowired
  private JwtTokenService jwtTokenService;

  @Autowired
  private AuthenticationManager authenticationManager;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  void authenticate_WithValidCredentials_ShouldReturnJwtToken() throws Exception {
    // Given
    AuthenticationRequest request = new AuthenticationRequest();
    request.setLogin(TEST_USERNAME);
    request.setPassword(TEST_PASSWORD);

    // Configure mocks for successful authentication
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(TEST_USERNAME,
        TEST_PASSWORD, null);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authToken);

    // Create a test user for authentication
    User testUser = UserFactory.createUser(TEST_USERNAME, "test@example.com", TEST_USERNAME);
    testUser.setPassword(TEST_PASSWORD_HASH);

    // Configure JwtUserDetailsService to return valid user details
    JwtUserDetails userDetails = new JwtUserDetails(
        testUser.getUuid(),
        testUser.getUsername(),
        testUser.getPassword(),
        List.of(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_USER)));
    when(jwtUserDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);

    // Configure JwtTokenService to return a valid token
    when(jwtTokenService.generateToken(any(UserDetails.class))).thenReturn("test.jwt.token");

    // When & Then
    mockMvc
        .perform(
            post(AUTHENTICATE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.accessToken").isNotEmpty());
  }

  @Test
  void authenticate_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
    // Given
    AuthenticationRequest request = new AuthenticationRequest();
    request.setLogin(TEST_USERNAME);
    request.setPassword("wrongpassword");

    // Configure mock to throw BadCredentialsException for invalid credentials
    when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

    // When & Then
    mockMvc
        .perform(
            post(AUTHENTICATE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void authenticate_WithNonExistentUser_ShouldReturnUnauthorized() throws Exception {
    // Given
    AuthenticationRequest request = new AuthenticationRequest();
    request.setLogin("nonexistentuser");
    request.setPassword(TEST_PASSWORD);

    // Configure mock to throw BadCredentialsException for non-existent user
    when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("User not found"));

    // When & Then
    mockMvc
        .perform(
            post(AUTHENTICATE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
