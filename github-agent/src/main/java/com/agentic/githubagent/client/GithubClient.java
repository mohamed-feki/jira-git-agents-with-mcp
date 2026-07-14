package com.agentic.githubagent.client;

import com.agentic.githubagent.dto.GithubCommitResponse;
import com.agentic.githubagent.dto.GithubRefResponse;
import com.agentic.githubagent.dto.GithubRepositoryResponse;
import com.agentic.githubagent.dto.GithubTreeEntry;
import com.agentic.githubagent.dto.GithubTreeResponse;

import java.util.List;

/**
 * Thin wrapper around the GitHub REST "Git Data" API, used to build a
 * single atomic commit containing multiple file creates/updates.
 */
public interface GithubClient {

    GithubRepositoryResponse getRepository(String owner, String repo);

    /**
     * Fetches the current head commit sha of a branch. If the branch does
     * not exist yet, implementations may create it from the repository's
     * default branch.
     */
    GithubRefResponse getBranchRef(String owner, String repo, String branch);

    String createBlob(String owner, String repo, String content);

    GithubTreeResponse createTree(String owner, String repo, String baseTreeSha, List<GithubTreeEntry> entries);

    GithubCommitResponse createCommit(String owner, String repo, String message, String treeSha, String parentCommitSha);

    void updateRef(String owner, String repo, String branch, String commitSha);

    /**
     * Creates a new branch ref pointing at the given commit sha.
     */
    void createRef(String owner, String repo, String branch, String commitSha);

    boolean branchExists(String owner, String repo, String branch);
}
