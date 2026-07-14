package com.agentic.githubagent.dto;

public record GithubBlobRequest(String content, String encoding) {
    public static GithubBlobRequest utf8(String content) {
        return new GithubBlobRequest(content, "utf-8");
    }
}
