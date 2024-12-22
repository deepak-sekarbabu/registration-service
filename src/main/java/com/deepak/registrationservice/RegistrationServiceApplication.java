package com.deepak.registrationservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableR2dbcRepositories
@EnableWebFlux
@OpenAPIDefinition(
    info =
        @Info(
            title = "Registration Service API",
            version = "1.0",
            description = "API for managing registrations and appointments"))
public class RegistrationServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(RegistrationServiceApplication.class, args);
  }
}
