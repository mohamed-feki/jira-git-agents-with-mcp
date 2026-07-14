package com.agentic.githubagent.dto;

public record PushResponse(
        String commitUrl,
        String commitSha,
        String repositoryUrl,
        int filesPushed,
        String branch
) {
}
