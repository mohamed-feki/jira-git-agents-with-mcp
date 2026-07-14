package com.agentic.githubagent.dto;

import java.util.List;

public record GithubCreateCommitRequest(String message, String tree, List<String> parents) {
}
