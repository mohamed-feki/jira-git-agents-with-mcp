package com.agentic.developeragent.dto;

/**
 * Token usage reported by the LLM provider for a single /develop call.
 * Populated from Spring AI's ChatResponse metadata (Usage), which both
 * the OpenAI and Ollama backends expose (though some local Ollama models
 * may only report a subset of these fields depending on the model).
 */
public record TokenUsage(
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens
) {
}
