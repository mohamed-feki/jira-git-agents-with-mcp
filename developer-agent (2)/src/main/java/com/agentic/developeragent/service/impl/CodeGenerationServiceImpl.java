package com.agentic.developeragent.service.impl;

import com.agentic.developeragent.dto.DevelopRequest;
import com.agentic.developeragent.dto.DevelopResponse;
import com.agentic.developeragent.dto.GeneratedFile;
import com.agentic.developeragent.dto.LlmGenerationRequest;
import com.agentic.developeragent.dto.LlmGenerationResult;
import com.agentic.developeragent.service.CodeGenerationService;
import com.agentic.developeragent.service.LlmService;
import com.agentic.developeragent.service.PromptService;
import com.agentic.developeragent.service.ResponseParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGenerationServiceImpl implements CodeGenerationService {

    private final PromptService promptService;
    private final LlmService llmService;
    private final ResponseParserService responseParserService;

    @Override
    public DevelopResponse develop(DevelopRequest request) {
        log.info("Generating code for ticket summary: '{}'", request.summary());

        LlmGenerationRequest generationRequest = new LlmGenerationRequest(
                request.summary(), request.description(), request.acceptanceCriteria());

        String prompt = promptService.buildPrompt(generationRequest);
        LlmGenerationResult llmResult = llmService.generate(prompt);
        List<GeneratedFile> files = responseParserService.parse(llmResult.content());

        log.info("LLM generated {} file(s), tokens used: {}", files.size(), llmResult.tokenUsage());
        return new DevelopResponse(files, llmResult.tokenUsage());
    }
}
