package com.agentic.developeragent.service;

import com.agentic.developeragent.dto.GeneratedFile;

import java.util.List;

public interface ResponseParserService {

    /**
     * Parses the raw LLM text response into a list of {@link GeneratedFile}.
     * Tolerates responses wrapped in markdown code fences.
     */
    List<GeneratedFile> parse(String rawLlmResponse);
}
