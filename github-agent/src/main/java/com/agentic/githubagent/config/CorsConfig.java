package com.agentic.githubagent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the Angular dev server (and any additional configured origins)
 * to call this API directly from the browser. Origins are configurable
 * via the "app.cors.allowed-origins" property so this can be locked down
 * per environment (e.g. a deployed frontend URL in production).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public CorsConfig(org.springframework.core.env.Environment env) {
        String configured = env.getProperty("app.cors.allowed-origins", "http://localhost:4200");
        this.allowedOrigins = configured.split(",");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
