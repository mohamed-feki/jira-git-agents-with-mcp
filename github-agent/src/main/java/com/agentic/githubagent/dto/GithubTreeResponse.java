package com.agentic.githubagent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubTreeResponse(String sha, String url) {
}
