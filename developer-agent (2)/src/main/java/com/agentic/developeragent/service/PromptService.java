package com.agentic.developeragent.service;

import com.agentic.developeragent.dto.LlmGenerationRequest;

public interface PromptService {

    /**
     * Builds the full prompt sent to the LLM for a given Jira ticket.
     */
    String buildPrompt(LlmGenerationRequest request);
}
