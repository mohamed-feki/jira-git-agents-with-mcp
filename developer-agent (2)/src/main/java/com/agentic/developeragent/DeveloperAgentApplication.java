package com.agentic.developeragent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DeveloperAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeveloperAgentApplication.class, args);
    }
}
