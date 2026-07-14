package com.agentic.developeragent.dto;

/**
 * Internal result of a single LLM call: the raw text content plus the
 * token usage reported alongside it.
 */
public record LlmGenerationResult(
        String content,
        TokenUsage tokenUsage
) {
}
