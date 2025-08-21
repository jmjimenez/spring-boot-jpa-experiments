package es.jmjg.experiments.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
@Validated
@Data
public class AppProperties {
  @NotBlank(message = "app.jwt-secret JWT secret is required")
  private String jwtSecret;
}
