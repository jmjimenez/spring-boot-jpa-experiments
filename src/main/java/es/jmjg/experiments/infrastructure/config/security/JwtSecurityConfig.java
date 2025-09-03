package es.jmjg.experiments.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
  public SecurityContextHolderStrategy securityContextHolderStrategy() {
    return SecurityContextHolder.getContextHolderStrategy();
  }

  @Bean
  public JwtRequestFilter jwtRequestFilter(
      final JwtUserDetailsService jwtUserDetailsService,
      final JwtTokenService jwtTokenService,
      final SecurityContextHolderStrategy securityContextHolderStrategy) {
    return new JwtRequestFilter(jwtUserDetailsService, jwtTokenService, securityContextHolderStrategy);
  }

  @Bean
  public SecurityFilterChain configure(
      final HttpSecurity http,
      final JwtUserDetailsService jwtUserDetailsService,
      final JwtTokenService jwtTokenService,
      final JwtRequestFilter jwtRequestFilter)
      throws Exception {
    return http
        .cors(cors -> cors.disable())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            authorize -> authorize
                .requestMatchers("/", "/authenticate", "/api-docs/**", "/swagger-ui/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/{uuid}")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/search")
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
            jwtRequestFilter,
            UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
