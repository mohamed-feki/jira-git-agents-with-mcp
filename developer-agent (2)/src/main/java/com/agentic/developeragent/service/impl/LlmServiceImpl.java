package com.agentic.developeragent.service.impl;

import com.agentic.developeragent.dto.LlmGenerationResult;
import com.agentic.developeragent.dto.TokenUsage;
import com.agentic.developeragent.exception.CodeGenerationException;
import com.agentic.developeragent.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {

    private final ChatClient chatClient;

    @Override
    public LlmGenerationResult generate(String prompt) {
        try {
            log.debug("Sending prompt to LLM ({} chars)", prompt.length());

            ChatResponse chatResponse = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .chatResponse();

            if (chatResponse == null || chatResponse.getResult() == null
                    || chatResponse.getResult().getOutput() == null) {
                throw new CodeGenerationException("LLM returned an empty response");
            }

            String content = chatResponse.getResult().getOutput().getText();
            if (content == null || content.isBlank()) {
                throw new CodeGenerationException("LLM returned an empty response");
            }

            TokenUsage tokenUsage = extractUsage(chatResponse);
            log.debug("Received LLM response ({} chars, tokens: prompt={}, completion={}, total={})",
                    content.length(), tokenUsage.promptTokens(), tokenUsage.completionTokens(), tokenUsage.totalTokens());

            return new LlmGenerationResult(content, tokenUsage);
        } catch (CodeGenerationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CodeGenerationException("LLM invocation failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Spring AI's Usage metadata is populated by both the OpenAI and Ollama
     * backends, though some local Ollama models only report a subset of
     * these fields (or none, depending on the model/runtime) - all three
     * are nullable in the response DTO for that reason.
     */
    private TokenUsage extractUsage(ChatResponse chatResponse) {
        Usage usage = chatResponse.getMetadata() != null ? chatResponse.getMetadata().getUsage() : null;
        if (usage == null) {
            return new TokenUsage(null, null, null);
        }
        Integer promptTokens = usage.getPromptTokens();
        Integer completionTokens = usage.getCompletionTokens();
        Integer totalTokens = usage.getTotalTokens();
        return new TokenUsage(promptTokens, completionTokens, totalTokens);
    }
}
