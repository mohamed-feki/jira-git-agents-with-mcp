package com.agentic.developeragent.dto;

import java.util.List;

/**
 * Response payload for POST /develop.
 */
public record DevelopResponse(
        List<GeneratedFile> files,
        TokenUsage tokenUsage
) {
}
