package com.agentic.githubagent.controller;

import com.agentic.githubagent.dto.PushRequest;
import com.agentic.githubagent.dto.PushResponse;
import com.agentic.githubagent.service.GithubService;
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
@RequestMapping("/github")
@RequiredArgsConstructor
@Tag(name = "GitHub Agent", description = "Commits and pushes generated files into a GitHub repository")
public class GithubController {

    private final GithubService githubService;

    @PostMapping("/push")
    @Operation(summary = "Push generated files", description = "Creates a single commit containing all given files and pushes it to the target branch.")
    public ResponseEntity<PushResponse> push(@Valid @RequestBody PushRequest request) {
        log.info("Received push request for repository '{}' branch '{}' with {} file(s)",
                request.repository(), request.branch(), request.files().size());
        PushResponse response = githubService.push(request);
        return ResponseEntity.ok(response);
    }
}
