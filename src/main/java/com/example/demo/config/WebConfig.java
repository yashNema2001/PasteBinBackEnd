package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // Apply to all API endpoints [cite: 25, 36, 55]
    registry.addMapping("/**")
        .allowedOrigins("*") // Allows any website to call your API
        .allowedMethods("GET", "POST", "OPTIONS") // Required for the app [cite: 27, 36, 55]
        .allowedHeaders("*") // Crucial to allow 'x-test-now-ms' [cite: 81]
        .exposedHeaders("x-test-now-ms");
  }
}
