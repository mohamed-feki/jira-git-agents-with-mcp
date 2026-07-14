package com.agentic.developeragent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the "agent.llm" section of application.yml.
 * Selects which Spring AI ChatModel implementation backs the Developer Agent.
 */
@ConfigurationProperties(prefix = "agent.llm")
public record LlmProperties(
        String provider,
        Double temperature,
        Integer maxTokens
) {
    public LlmProperties {
        if (provider == null || provider.isBlank()) {
            provider = "openai";
        }
        if (temperature == null) {
            temperature = 0.2;
        }
        if (maxTokens == null) {
            maxTokens = 4000;
        }
    }

    public boolean isOllama() {
        return "ollama".equalsIgnoreCase(provider);
    }

    public boolean isOpenAi() {
        return "openai".equalsIgnoreCase(provider);
    }
}
