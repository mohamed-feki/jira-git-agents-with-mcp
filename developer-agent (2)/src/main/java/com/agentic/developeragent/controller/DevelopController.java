package com.agentic.developeragent.controller;

import com.agentic.developeragent.dto.DevelopRequest;
import com.agentic.developeragent.dto.DevelopResponse;
import com.agentic.developeragent.service.CodeGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Developer Agent", description = "Asks an LLM to generate production-ready code for a Jira ticket")
public class DevelopController {

    private final CodeGenerationService codeGenerationService;

    @PostMapping("/develop")
    @Operation(summary = "Generate code for a ticket", description = "Sends the ticket description to the configured LLM and returns generated files.")
    public ResponseEntity<DevelopResponse> develop(@Valid @RequestBody DevelopRequest request) {
        log.info("Received /develop request for summary: '{}'", request.summary());
        DevelopResponse response = codeGenerationService.develop(request);
        return ResponseEntity.ok(response);
    }
}
