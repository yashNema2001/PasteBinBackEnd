package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${app.base-url}")
  private String frontendUrl;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins(frontendUrl) // This must match your Vercel URL
        .allowedMethods("GET", "POST", "OPTIONS")
        .allowedHeaders("*") // Crucial for x-test-now-ms [cite: 81]
        .allowCredentials(true);
  }
}
