package com.agentic.githubagent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI githubAgentOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("GitHub Agent API")
                        .description("Commits and pushes generated files into a GitHub repository.")
                        .version("1.0.0"));
    }
}
