package es.jmjg.experiments.infrastructure.config.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
  private final UserDetailsService userDetailsService;
  private final JwtTokenService jwtTokenService;

  public JwtRequestFilter(
      final UserDetailsService userDetailsService, final JwtTokenService jwtTokenService) {
    this.userDetailsService = userDetailsService;
    this.jwtTokenService = jwtTokenService;
  }

  @Override
  protected void doFilterInternal(
      final @NonNull HttpServletRequest request,
      final @NonNull HttpServletResponse response,
      final @NonNull FilterChain chain)
      throws IOException, ServletException {
    // look for Bearer auth header
    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    log.debug("Authorization header: {}", header);

    if (header == null || !header.startsWith("Bearer ")) {
      log.debug("No Bearer token found, continuing without authentication");
      chain.doFilter(request, response);
      return;
    }

    final String token = header.substring(7);
    log.debug("Extracted token length: {}", token.length());

    final DecodedJWT jwt = jwtTokenService.validateToken(token);
    if (jwt == null || jwt.getSubject() == null) {
      // validation failed or token expired
      log.debug("Token validation failed or token expired");
      chain.doFilter(request, response);
      return;
    }

    log.debug("Token validated successfully for user: {}", jwt.getSubject());

    final UserDetails userDetails;
    try {
      userDetails = userDetailsService.loadUserByUsername(jwt.getSubject());
    } catch (final UsernameNotFoundException userNotFoundEx) {
      // user not found
      log.debug("User not found: {}", jwt.getSubject());
      chain.doFilter(request, response);
      return;
    }

    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
    authentication.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request));

    // set user details on spring security context
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.debug("Authentication set for user: {}", userDetails.getUsername());

    // continue with authenticated user
    chain.doFilter(request, response);
  }
}
