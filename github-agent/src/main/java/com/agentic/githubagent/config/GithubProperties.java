package com.agentic.githubagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the "github" section of application.yml.
 * Holds default GitHub REST API connection details. The token is always
 * taken from configuration/environment (never accepted in a request body)
 * for security. Owner/repository defaults may be overridden per-request.
 */
@ConfigurationProperties(prefix = "github")
public record GithubProperties(
        String apiBaseUrl,
        String token,
        String owner,
        String repository,
        String committerName,
        String committerEmail,
        long connectTimeoutMs,
        long responseTimeoutMs
) {
    public GithubProperties {
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            apiBaseUrl = "https://api.github.com";
        }
        if (committerName == null || committerName.isBlank()) {
            committerName = "agentic-github-agent";
        }
        if (committerEmail == null || committerEmail.isBlank()) {
            committerEmail = "agentic-github-agent@users.noreply.github.com";
        }
        if (connectTimeoutMs <= 0) {
            connectTimeoutMs = 5000;
        }
        if (responseTimeoutMs <= 0) {
            responseTimeoutMs = 20000;
        }
    }
}
