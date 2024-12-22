package com.deepak.registrationservice.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Bean
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();

    // Allow all origins - you should restrict this in production
    corsConfig.setAllowedOrigins(
        Arrays.asList("http://localhost:3000", "http://localhost:8082", "https://localhost:8444"));

    // Allow common HTTP methods
    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));

    // Allow common headers
    corsConfig.setAllowedHeaders(
        Arrays.asList(
            "Origin",
            "Content-Type",
            "Accept",
            "Authorization",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"));

    // Allow credentials (cookies, authorization headers, etc.)
    corsConfig.setAllowCredentials(true);

    // Expose headers that clients are allowed to access
    corsConfig.setExposedHeaders(
        Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Authorization"));

    // How long the browser should cache CORS response (in seconds)
    corsConfig.setMaxAge(3600L);

    // Create the source with the configuration
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }
}
