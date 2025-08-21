package es.jmjg.experiments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ExperimentsApplication {

  public static void main(String[] args) {
    SpringApplication.run(ExperimentsApplication.class, args);
  }
}
