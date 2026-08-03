package com.momento;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties
@OpenAPIDefinition(info = @Info(
    title = "Momento Service", description = "Face recognition service"))
public class Application {
  public static void main(String[] args) {
    final SpringApplication app = new SpringApplication(Application.class);
    app.setApplicationStartup(new BufferingApplicationStartup(2048));
    app.run(args);
  }
}
