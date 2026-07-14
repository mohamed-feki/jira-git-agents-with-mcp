package com.agentic.developeragent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Builds the ChatClient used by the Developer Agent, selecting between the
 * OpenAI-backed and Ollama-backed chat models based on {@code agent.llm.provider}.
 * Both starters are on the classpath; Spring Boot autoconfigures both
 * {@link OpenAiChatModel} and {@link OllamaChatModel} beans (each guarded by
 * its own connection properties), and this configuration picks the one the
 * operator selected.
 */
@Slf4j
@Configuration
public class ChatClientConfig {

    @Bean
    @Primary
    public ChatClient chatClient(LlmProperties llmProperties,
                                  @Autowired(required = false) @Qualifier("openAiChatModel") OpenAiChatModel openAiChatModel,
                                  @Autowired(required = false) @Qualifier("ollamaChatModel") OllamaChatModel ollamaChatModel) {

        if (llmProperties.isOllama()) {
            if (ollamaChatModel == null) {
                throw new IllegalStateException("agent.llm.provider=ollama but no OllamaChatModel bean is available. " +
                        "Check spring.ai.ollama.* configuration.");
            }
            log.info("Developer Agent configured to use Ollama as the LLM provider");
            return ChatClient.builder(ollamaChatModel).build();
        }

        if (openAiChatModel == null) {
            throw new IllegalStateException("agent.llm.provider=openai but no OpenAiChatModel bean is available. " +
                    "Check spring.ai.openai.* configuration.");
        }
        log.info("Developer Agent configured to use OpenAI as the LLM provider");
        return ChatClient.builder(openAiChatModel).build();
    }
}
