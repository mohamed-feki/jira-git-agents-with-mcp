package com.agentic.githubagent.exception;

public class RepositoryNotFoundException extends RuntimeException {
    public RepositoryNotFoundException(String repository) {
        super("GitHub repository not found or inaccessible: " + repository);
    }
}
