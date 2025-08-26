package es.jmjg.experiments.infrastructure.controller.authentication;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.config.security.JwtTokenService;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetails;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetailsService;
import es.jmjg.experiments.infrastructure.controller.authentication.dto.AuthenticationRequestDto;
import es.jmjg.experiments.shared.TestDataSamples;
import es.jmjg.experiments.shared.UserFactory;

@WebMvcTest(AuthenticationController.class)
@Import(AuthenticationControllerTest.AuthenticationTestConfig.class)
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

  @TestConfiguration
  @EnableWebSecurity
  static class AuthenticationTestConfig {

    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
      return mock(AuthenticationManager.class);
    }

    @Bean
    @Primary
    public JwtUserDetailsService jwtUserDetailsService() {
      JwtUserDetailsService mockService = mock(JwtUserDetailsService.class);

      // Configure the mock to return user details with ROLE_USER authority for any
      // username
      when(mockService.loadUserByUsername(anyString()))
          .thenAnswer(invocation -> {
            String username = invocation.getArgument(0);

            // Special case for admin user
            if (TestDataSamples.ADMIN_USERNAME.equals(username)) {
              User user = UserFactory.createUser(TestDataSamples.ADMIN_UUID, TestDataSamples.ADMIN_NAME,
                  TestDataSamples.ADMIN_EMAIL, TestDataSamples.ADMIN_USERNAME);
              return UserFactory.createUserUserDetails(user);
            }

            // For Leanne user
            if (TestDataSamples.LEANNE_USERNAME.equals(username)) {
              User user = UserFactory.createUser(TestDataSamples.LEANNE_UUID, TestDataSamples.LEANNE_NAME,
                  TestDataSamples.LEANNE_EMAIL, TestDataSamples.LEANNE_USERNAME);
              return UserFactory.createUserUserDetails(user);
            }

            // For all other users, return with ROLE_USER authority
            User user = UserFactory.createUser(UUID.randomUUID(), "Test User", "test@example.com", username);
            return UserFactory.createUserUserDetails(user);
          });

      return mockService;
    }

    @Bean
    @Primary
    public JwtTokenService jwtTokenService() {
      JwtTokenService mockService = mock(JwtTokenService.class);

      // Configure the mock to return a valid JWT for any token
      when(mockService.validateToken(anyString()))
          .thenAnswer(invocation -> {
            String token = invocation.getArgument(0);

            // Create a mock DecodedJWT that returns the token as the username
            DecodedJWT mockJwt = mock(DecodedJWT.class);
            when(mockJwt.getSubject()).thenReturn(token); // Use token as username for simplicity

            return mockJwt;
          });

      return mockService;
    }

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http
          .cors(cors -> cors.disable())
          .csrf(csrf -> csrf.disable())
          .authorizeHttpRequests(
              authorize -> authorize
                  .requestMatchers("/", "/authenticate", "/api-docs/**", "/swagger-ui/**")
                  .permitAll()
                  .anyRequest()
                  .authenticated())
          .sessionManagement(
              session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .exceptionHandling(
              exceptionHandling -> exceptionHandling
                  .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
          .build();
    }
  }
}
