package es.jmjg.experiments.infrastructure.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Authentication response containing the JWT access token")
public class AuthenticationResponse {
  @Schema(description = "JWT access token for API authentication", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9...")
  private String accessToken;

  @Schema(description = "OAuth2 access token (alias for accessToken)", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9...")
  private String access_token;

  @Schema(description = "Token type", example = "bearer")
  private String tokenType = "bearer";

  @Schema(description = "Token expiration time in seconds", example = "1800")
  private Integer expiresIn = 1800;

  // Setter to ensure both fields are set
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
    this.access_token = accessToken; // OAuth2 compatibility
  }
}
