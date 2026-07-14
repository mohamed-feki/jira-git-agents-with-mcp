package com.agentic.developeragent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI developerAgentOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Developer Agent API")
                        .description("Asks an LLM to implement a Jira ticket as production-ready code.")
                        .version("1.0.0"));
    }
}
