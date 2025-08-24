package es.jmjg.experiments.infrastructure.controller.authentication;

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
import es.jmjg.experiments.infrastructure.config.security.JwtTokenService;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetailsService;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationRequestDto;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;

@WebMvcTest(AuthenticationController.class)
@Import(ControllerTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthenticationControllerTest {

  private static final String AUTHENTICATE_ENDPOINT = "/authenticate";

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
    AuthenticationRequestDto request = new AuthenticationRequestDto();
    request.setLogin(TestDataSamples.ADMIN_USERNAME);
    request.setPassword(TestDataSamples.ADMIN_PASSWORD);

    // Configure mocks for successful authentication
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        TestDataSamples.ADMIN_USERNAME,
        TestDataSamples.ADMIN_PASSWORD, null);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authToken);

    // Create a test user for authentication
    User testUser = UserFactory.createUser(TestDataSamples.ADMIN_USERNAME, TestDataSamples.ADMIN_EMAIL,
        TestDataSamples.ADMIN_USERNAME);
    testUser.setPassword(TestDataSamples.ADMIN_PASSWORD_HASH);

    // Configure JwtUserDetailsService to return valid user details
    JwtUserDetails userDetails = new JwtUserDetails(
        testUser.getUuid(),
        testUser.getUsername(),
        testUser.getPassword(),
        List.of(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_USER)));
    when(jwtUserDetailsService.loadUserByUsername(TestDataSamples.ADMIN_USERNAME)).thenReturn(userDetails);

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
    AuthenticationRequestDto request = new AuthenticationRequestDto();
    request.setLogin(TestDataSamples.ADMIN_USERNAME);
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
    AuthenticationRequestDto request = new AuthenticationRequestDto();
    request.setLogin("nonexistentuser");
    request.setPassword(TestDataSamples.ADMIN_PASSWORD);

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
