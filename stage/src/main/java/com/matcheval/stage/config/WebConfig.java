package com.matcheval.stage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:4200",
                                "https://matcheval-frontend.onrender.com",
                                "https://matcheval-ia-1.onrender.com"
                        )
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
