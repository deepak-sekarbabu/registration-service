package com.deepak.registrationservice;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableR2dbcRepositories
@EnableWebFlux
public class RegistrationServiceApplication {

  @Value("${app.version:1.0}")
  private String appVersion;

  public static void main(String[] args) {
    SpringApplication.run(RegistrationServiceApplication.class, args);
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new io.swagger.v3.oas.models.info.Info()
                .title("Registration Service API")
                .version(appVersion)
                .description("API for managing registrations and appointments"))
        .servers(List.of(new Server().url("http://localhost:8444").description("Local Server")));
  }
}
