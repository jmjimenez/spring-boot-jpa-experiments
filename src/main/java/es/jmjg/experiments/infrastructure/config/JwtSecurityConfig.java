package es.jmjg.experiments.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.jmjg.experiments.infrastructure.security.JwtRequestFilter;
import es.jmjg.experiments.infrastructure.security.JwtTokenService;
import es.jmjg.experiments.infrastructure.security.JwtUserDetailsService;

@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      final AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public JwtRequestFilter jwtRequestFilter(
      final JwtUserDetailsService jwtUserDetailsService, final JwtTokenService jwtTokenService) {
    return new JwtRequestFilter(jwtUserDetailsService, jwtTokenService);
  }

  @Bean
  public SecurityFilterChain configure(
      final HttpSecurity http,
      final JwtUserDetailsService jwtUserDetailsService,
      final JwtTokenService jwtTokenService)
      throws Exception {
    return http
        .cors(cors -> cors.disable())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            authorize -> authorize
                .requestMatchers("/", "/authenticate", "/api-docs/**", "/swagger-ui/**")
                .permitAll()
                .requestMatchers("/api/posts")
                .permitAll()
                .requestMatchers("/api/posts/{uuid}")
                .permitAll()
                .requestMatchers("/api/**")
                .hasAuthority(JwtUserDetailsService.ROLE_USER)
                .anyRequest()
                .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .addFilterBefore(
            jwtRequestFilter(jwtUserDetailsService, jwtTokenService),
            UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
