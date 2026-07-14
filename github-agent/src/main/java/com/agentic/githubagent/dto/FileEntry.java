package com.agentic.githubagent.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * A single file to create or update in the target repository.
 */
public record FileEntry(
        @NotBlank(message = "path must not be blank")
        String path,

        @NotBlank(message = "content must not be blank")
        String content
) {
}
