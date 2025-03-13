package com.timetracker.sistema_gerenciamento.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://sistema-horas-front.herokuapp.com",
                        "http://localhost:4200",
                        "https://sistema-horas-front-c24bebf44baf.herokuapp.com",
                        "/meu-app/swagger-ui.html",
                        "/meu-app/v3/api-docs/**"
                )
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("*");
    }
}