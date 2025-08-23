package es.jmjg.experiments.infrastructure.config.security;

import java.time.Duration;
import java.time.Instant;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import es.jmjg.experiments.infrastructure.config.AppProperties;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtTokenService {
  private static final Duration JWT_TOKEN_VALIDITY = Duration.ofMinutes(30);
  private final Algorithm hmac512;
  private final JWTVerifier verifier;

  public JwtTokenService(final AppProperties appProperties) {
    this.hmac512 = Algorithm.HMAC512(appProperties.getJwtSecret());
    this.verifier = JWT.require(this.hmac512).build();
  }

  public String generateToken(final UserDetails userDetails) {
    final Instant now = Instant.now();
    return JWT.create()
        .withSubject(userDetails.getUsername())
        .withIssuer("app")
        .withIssuedAt(now)
        .withExpiresAt(now.plus(JWT_TOKEN_VALIDITY))
        .sign(this.hmac512);
  }

  public DecodedJWT validateToken(final String token) {
    try {
      return verifier.verify(token);
    } catch (final JWTVerificationException verificationEx) {
      log.warn("token invalid: {}", verificationEx.getMessage());
      return null;
    }
  }
}
