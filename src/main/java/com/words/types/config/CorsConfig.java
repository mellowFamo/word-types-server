package com.words.types.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  private final String apiPrefix;
  private final List<String> allowedOrigins;

  public CorsConfig(
      @Value("${app.api-prefix}") String apiPrefix,
      @Value("${app.cors.allowed-origins:*}") List<String> allowedOrigins) {
    this.apiPrefix = apiPrefix;
    this.allowedOrigins = allowedOrigins;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping(apiPrefix + "/**")
      .allowedOrigins(allowedOrigins.toArray(String[]::new))
      .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
      .allowedHeaders("*");
  }
}