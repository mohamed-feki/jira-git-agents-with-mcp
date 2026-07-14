package com.agentic.githubagent.service;

import com.agentic.githubagent.dto.PushRequest;
import com.agentic.githubagent.dto.PushResponse;

public interface GithubService {

    /**
     * Creates/updates the given files as a single commit on the target
     * branch and pushes it to GitHub.
     */
    PushResponse push(PushRequest request);
}
