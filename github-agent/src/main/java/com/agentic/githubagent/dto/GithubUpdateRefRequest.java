package com.agentic.githubagent.dto;

public record GithubUpdateRefRequest(String sha, boolean force) {
}
