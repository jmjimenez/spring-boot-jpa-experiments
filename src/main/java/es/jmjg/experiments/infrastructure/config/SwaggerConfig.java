package es.jmjg.experiments.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Spring Boot JPA Experiments API")
                .description(
                    "API documentation for Spring Boot JPA experiments project using hexagonal architecture")
                .version("1.0.0")
                .contact(
                    new Contact()
                        .name("Jose JMG")
                        .email("contact@example.com")
                        .url("https://github.com/yourusername/spring-boot-jpa-experiments"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server().url("http://localhost:8080").description("Local development server"),
                new Server()
                    .url("https://your-production-domain.com")
                    .description("Production server")));
  }
}
