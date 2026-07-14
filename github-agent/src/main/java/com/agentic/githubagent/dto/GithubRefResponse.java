package com.agentic.githubagent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRefResponse(String ref, GithubRefObject object) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GithubRefObject(String sha, String type, String url) {
    }
}
