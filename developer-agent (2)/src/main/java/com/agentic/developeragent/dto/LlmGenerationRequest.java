package com.agentic.developeragent.dto;

/**
 * Internal value object passed from the controller into the code generation
 * pipeline (prompt building -> LLM call -> response parsing).
 */
public record LlmGenerationRequest(
        String summary,
        String description,
        String acceptanceCriteria
) {
}
