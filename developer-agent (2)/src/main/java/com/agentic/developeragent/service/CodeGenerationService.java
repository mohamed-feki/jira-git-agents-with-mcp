package com.agentic.developeragent.service;

import com.agentic.developeragent.dto.DevelopRequest;
import com.agentic.developeragent.dto.DevelopResponse;

public interface CodeGenerationService {

    /**
     * Orchestrates prompt building, LLM invocation, and response parsing
     * for a given Jira ticket description.
     */
    DevelopResponse develop(DevelopRequest request);
}
