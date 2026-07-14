package com.agentic.githubagent.dto;

public record GithubTreeEntry(String path, String mode, String type, String sha) {
    public static GithubTreeEntry blob(String path, String sha) {
        return new GithubTreeEntry(path, "100644", "blob", sha);
    }
}
