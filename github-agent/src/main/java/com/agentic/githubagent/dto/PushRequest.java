package com.agentic.githubagent.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Incoming request payload for POST /github/push.
 */
public record PushRequest(
        @NotBlank(message = "repository must not be blank")
        String repository,

        @NotBlank(message = "branch must not be blank")
        String branch,

        @NotBlank(message = "commitMessage must not be blank")
        String commitMessage,

        @NotEmpty(message = "files must not be empty")
        @Valid
        List<FileEntry> files
) {
}
