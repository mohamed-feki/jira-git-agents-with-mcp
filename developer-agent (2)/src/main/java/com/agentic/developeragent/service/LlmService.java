package com.agentic.developeragent.service;

import com.agentic.developeragent.dto.LlmGenerationResult;

public interface LlmService {

    /**
     * Sends the given prompt to the configured LLM and returns both the
     * raw text response and the token usage reported for the call.
     */
    LlmGenerationResult generate(String prompt);
}
