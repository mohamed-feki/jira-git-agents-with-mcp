package com.agentic.developeragent.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Incoming request payload for POST /develop.
 * Mirrors the ticket fields produced by the Jira Agent.
 */
public record DevelopRequest(
        @NotBlank(message = "summary must not be blank")
        String summary,

        @NotBlank(message = "description must not be blank")
        String description,

        String acceptanceCriteria
) {
}
